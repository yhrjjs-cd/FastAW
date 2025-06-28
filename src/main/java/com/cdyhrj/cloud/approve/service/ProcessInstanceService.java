package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.api.IAwMessageSender;
import com.cdyhrj.cloud.approve.api.IUserContext;
import com.cdyhrj.cloud.approve.domain.ApprovalInfo;
import com.cdyhrj.cloud.approve.domain.ApprovalItem;
import com.cdyhrj.cloud.approve.domain.ForwardInfo;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.StartProcessInfo;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.StepInfo;
import com.cdyhrj.cloud.approve.domain.flow.Flow;
import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import com.cdyhrj.cloud.approve.entity.CcItem;
import com.cdyhrj.cloud.approve.entity.CommentItem;
import com.cdyhrj.cloud.approve.entity.ForwardEmployeeEntity;
import com.cdyhrj.cloud.approve.entity.ForwardInfoEntity;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.entity.ProcessInstanceAttachment;
import com.cdyhrj.cloud.approve.entity.ProcessInstanceBizRelation;
import com.cdyhrj.cloud.approve.entity.ProcessInstanceWf;
import com.cdyhrj.cloud.approve.entity.Task;
import com.cdyhrj.cloud.approve.entity.TaskItem;
import com.cdyhrj.cloud.approve.entity.TaskItemWithResult;
import com.cdyhrj.cloud.approve.entity.Variable;
import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.stringtemplate.v4.ST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus.Running;

/**
 * 流程实例服务
 *
 * @author 黄奇
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessInstanceService {
    private final FastORM fastORM;
    private final StartService startService;
    private final TemplateService templateService;
    private final BusinessService businessService;
    private final IUserContext userContext;
    private final IAwMessageSender messageService;

    /**
     * 启动流程
     *
     * @param startProcessInfo 启动信息
     * @return 流程Id
     */
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance start(StartProcessInfo startProcessInfo) {
        String promoterName = startProcessInfo.getPromoterName();

        ProcessInstance processInstance = ProcessInstance.builder()
                .bizType(startProcessInfo.getBizType())
                .bizId(startProcessInfo.getBizId())
                .promoterId(startProcessInfo.getPromoter())
                .promoterName(promoterName)
                .title(startProcessInfo.getRuntimeWf().getTitle())
                .description(calcDescription(startProcessInfo))
                .startTime(LocalDateTime.now())
                .status(Running)
                .build();

        processInstance.setTenantId(userContext.getTenantId());

        ProcessInstanceWf processInstanceWf = new ProcessInstanceWf();
        processInstanceWf.setRuntimeWf(startProcessInfo.getRuntimeWf());

        processInstance.setProcessInstanceWf(processInstanceWf);

        // 保存流程实例
        fastORM.insertable(processInstance)
                .withRelation(ProcessInstance::getProcessInstanceWf)
                .insert();
//        List<IdName> attachments = startProcessInfo.getAttachments();
//        if (Objects.nonNull(attachments)) {
//            for (IdName idName : attachments) {
//                ProcessInstanceAttachment processInstanceAttachment = ProcessInstanceAttachment.builder()
//                        .processInstanceId(processInstance.getId())
//                        .id(idName.getId())
//                        .name(idName.getName())
//                        .build();
//
//                sqlClient.inserter(ProcessInstanceAttachment.class).insert(processInstanceAttachment);
//            }
//        }


        ProcessInstanceBizRelation processInstanceBizRelation = ProcessInstanceBizRelation.builder()
                .title(startProcessInfo.getRuntimeWf().getTitle())
                .bizType(startProcessInfo.getBizType())
                .bizId(startProcessInfo.getBizId())
                .processInstanceId(processInstance.getId())
                .processStatus(processInstance.getStatus())
                .processInfo(this.getProcessInfo(startProcessInfo))
                .build();
        processInstanceBizRelation.setTenantId(processInstance.getTenantId());

        fastORM.insertable(processInstanceBizRelation).insert();

        // 启动第一个任务
        startService.start(startProcessInfo, processInstance);

        return processInstance;
    }

    /**
     * 根据业务数据计算流程描述信息
     *
     * @param startProcessInfo 启动信息
     * @return 描述信息
     */
    private String calcDescription(StartProcessInfo startProcessInfo) {
        String templateId = startProcessInfo.getRuntimeWf().getTemplateId();
        String templateContent = templateService.getConfig(templateId).getContentTemplate();
        if (Objects.isNull(templateContent)) {
            return String.valueOf(startProcessInfo.getBizData());
        } else {
            ST content = new ST(templateContent, '{', '}');
            content.add("data", startProcessInfo.getBizData());
            content.add("promoter", userContext.getUserName());

            return content.render();
        }
    }

    /**
     * 根据启动信息获取流程描述信息（最大100字）
     * 格式：审批节点-第一个人等N人（审批中）
     *
     * @param startProcessInfo 启动信息
     * @return 流程描述实例信息
     */
    private String getProcessInfo(StartProcessInfo startProcessInfo) {
        Step step = startProcessInfo.getRuntimeWf()
                .getSteps()
                .get(0);
        Objects.requireNonNull(step, "审批节点不存在，请检查审批模版");

        return StepUtils.extractStepTipInfo(step);
    }


    /**
     * 结束流程
     */
    public void end(ProcessInstance processInstance) {
        // 任务执行完成，结束流程
        processInstance.setEndTime(LocalDateTime.now());

        TaskStatus status = TaskStatus.valueOf(this.getVariable(processInstance, Variable.KEY_RESULT));
        if (status == TaskStatus.Approved) {
            processInstance.setStatus(ProcessInstanceStatus.Finished);

            fastORM.updatable(ProcessInstance.class)
                    .id(processInstance.getId())
                    .updateField(
                            ProcessInstance::getEndTime, processInstance.getEndTime(),
                            ProcessInstance::getCurrTask, null,
                            ProcessInstance::getCurrTaskExecutors, null,
                            ProcessInstance::getStatus, processInstance.getStatus()
                    );
            fastORM.updatable(ProcessInstanceBizRelation.class)
                    .where()
                    .andEq(ProcessInstanceBizRelation::getProcessInstanceId, processInstance.getId())
                    .ret()
                    .updateField(
                            ProcessInstanceBizRelation::getProcessInfo, "已通过",
                            ProcessInstanceBizRelation::getProcessStatus, ProcessInstanceStatus.Finished
                    );
        } else if (status == TaskStatus.Rejected) {
            processInstance.setStatus(ProcessInstanceStatus.Failure);

            fastORM.updatable(ProcessInstance.class)
                    .id(processInstance.getId())
                    .updateField(ProcessInstance::getEndTime, processInstance.getEndTime(),
                            ProcessInstance::getCurrTask, null,
                            ProcessInstance::getCurrTaskExecutors, null,
                            ProcessInstance::getStatus, processInstance.getStatus()
                    );

            fastORM.updatable(ProcessInstanceBizRelation.class)
                    .where()
                    .andEq(ProcessInstanceBizRelation::getProcessInstanceId, processInstance.getId())
                    .ret()
                    .updateField(
                            ProcessInstanceBizRelation::getProcessInfo, "未通过",
                            ProcessInstanceBizRelation::getProcessStatus, ProcessInstanceStatus.Failure
                    );
        } else {
            // TODO 输出异常
        }

        this.completeProcessInstance(processInstance);
    }

    /**
     * 获取流程实例
     *
     * @param processInstanceId 流程Id
     * @return 流程实例
     */
    public ProcessInstance fetchProcessInstance(Long processInstanceId) {
        return fastORM.fetchable(ProcessInstance.class)
                .id(processInstanceId)
                .fetch()
                .orElseThrow();
    }

    /**
     * 撤销消息
     *
     * @param processInstanceId 流程Id
     * @return 当前Guid
     */
    @Transactional(rollbackFor = Exception.class)
    public Long cancelProcess(Long processInstanceId) {
        ProcessInstance processInstance = fetchProcessInstance(processInstanceId);

        Assert.notNull(processInstance, "流程不存在：" + processInstanceId);
        Assert.isTrue(processInstance.getStatus() == ProcessInstanceStatus.Running, "流程已完成，不能撤销");

        fastORM.updatable(ProcessInstance.class)
                .id(processInstanceId)
                .updateField(
                        ProcessInstance::getEndTime, new Date(),
                        ProcessInstance::getCurrTaskExecutors, null,
                        ProcessInstance::getStatus, ProcessInstanceStatus.Canceled
                );

        fastORM.updatable(Task.class)
                .where()
                .andEq(Task::getProcessInstanceId, processInstanceId)
                .andOrGroup()
                .orEq(Task::getStatus, TaskStatus.Running)
                .orEq(Task::getStatus, TaskStatus.Created)
                .end()
                .ret()
                .updateField(Task::getStatus, TaskStatus.Canceled,
                        Task::getEndTime, new Date());

        fastORM.updatable(TaskItem.class)
                .where()
                .andEq(TaskItem::getProcessInstanceId, processInstanceId)
                .andOrGroup()
                .orEq(TaskItem::getStatus, TaskStatus.Running)
                .orEq(TaskItem::getStatus, TaskStatus.Created)
                .end()
                .ret()
                .updateField(TaskItem::getStatus, TaskStatus.Canceled, TaskItem::getEndTime, new Date());

        fastORM.updatable(ProcessInstanceBizRelation.class)
                .where()
                .andEq(ProcessInstanceBizRelation::getProcessInstanceId, processInstance.getId())
                .ret()
                .updateField(
                        ProcessInstanceBizRelation::getProcessInfo, "已撤销",
                        ProcessInstanceBizRelation::getProcessStatus, ProcessInstanceStatus.Canceled
                );

        this.completeProcessInstance(processInstance);

        return processInstanceId;
    }

    /**
     * 获取变量
     *
     * @param processInstance 流程实例
     * @param key             键值
     * @return 变量
     */
    public String getVariable(ProcessInstance processInstance, String key) {
        Optional<Variable> variable = fastORM.fetchable(Variable.class)
                .where()
                .andEq(Variable::getProcessInstanceId, processInstance.getId())
                .andEq(Variable::getName, key)
                .ret()
                .fetch();

        if (variable.isEmpty()) {
            return "";
        } else {
            return variable.get().getValue();
        }
    }

    /**
     * 设置变量
     *
     * @param processInstance 流程实例
     * @param key             键值
     * @param value           变量
     */
    public void setVariable(ProcessInstance processInstance, String key, String value) {
        fastORM.fetchable(Variable.class)
                .where()
                .andEq(Variable::getProcessInstanceId, processInstance.getId())
                .andEq(Variable::getName, key)
                .ret()
                .fetch()
                .ifPresentOrElse(variable -> {
                    variable.setValue(value);

                    fastORM.updatable(variable).update();
                }, () -> {
                    Variable variable = new Variable();
                    variable.setName(key);
                    variable.setValue(value);
                    variable.setProcessInstanceId(processInstance.getId());

                    fastORM.insertable(variable).insert();
                });
    }

    /**
     * 评论
     *
     * @param commentItem 评论项
     * @return 评论项Id
     */
    public Long comment(CommentItem commentItem) {
        fastORM.insertable(commentItem).insert();

        return commentItem.getCommentId();
    }

    /**
     * 添加转发人
     *
     * @param forwardInfo 转发信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void forward(ForwardInfo forwardInfo) {
        Objects.requireNonNull(forwardInfo.getPersonList(), "转发人不能为空");

        Long userId = userContext.getUserId();

        List<String> personNames = forwardInfo.getPersonList()
                .stream()
                .map(IdName::getName)
                .toList();
        String personStr = StringUtils.abbreviate(StringUtils.join(personNames, ","), 80);
        ForwardInfoEntity forwardInfoEntity = ForwardInfoEntity.builder()
                .processInstanceId(forwardInfo.getProcessInstanceId())
                .fromEmpId(userId)
                .fromEmpName(userContext.getUserName())
                .isApproval(forwardInfo.isApproval())
                .comments(personStr)
                .info(forwardInfo.getInfo())
                .build();

        List<ForwardEmployeeEntity> employeeList = new ArrayList<>();
        for (IdName person : forwardInfo.getPersonList()) {
            employeeList.add(ForwardEmployeeEntity.builder()
                    .processInstanceId(forwardInfo.getProcessInstanceId())
                    .empId(person.getId())
                    .fromEmpId(userId)
                    .build());
        }

        fastORM.insertable(forwardInfoEntity).insert();
        fastORM.insertable(employeeList).insert();
    }

    /**
     * 获取步骤信息
     *
     * @param id        模版Id
     * @param dataValue 待提交的数据值
     * @return 步骤信息
     */
    public StepInfo getStepInfo(String id, Map<String, Object> dataValue) {
        StepInfo stepInfo = new StepInfo();

        Flow flow = templateService.getConfig(id);
        stepInfo.setTemplateId(id);
        stepInfo.setTitle(flow.getTitle());

        List<Step> steps = new ArrayList<>();

        WorkNode next = flow.getNext();
        next.writeStepsTo(steps, dataValue);

        stepInfo.setSteps(steps);

        return stepInfo;
    }

    /**
     * 获取审批项
     *
     * @param processInstanceId 流程实例Id
     * @return 审批项清单
     */
    public ApprovalInfo getApprovalInfo(Long processInstanceId) {
        ProcessInstance processInstance = fetchProcessInstance(processInstanceId);

        Objects.requireNonNull(processInstance, "审批信息不存在");

        List<ApprovalItem> items = getAllItems(processInstance);
        List<IdName> attachments = getAttachments(processInstanceId);

        return ApprovalInfo.builder()
                .promoterId(processInstance.getPromoterId())
                .promoter(processInstance.getPromoterName())
                .startTime(processInstance.getStartTime())
                .attachments(attachments)
                .items(items)
                .status(processInstance.getStatus())
                .build();
    }

    /**
     * 获取所有项
     *
     * @param processInstance 流程实例
     * @return 审批项
     */
    private List<ApprovalItem> getAllItems(ProcessInstance processInstance) {
        ApprovalItem startItem = getStartItem(processInstance);

        List<ApprovalItem> approvalItems = getApprovalItems(processInstance.getId());
        List<ApprovalItem> commentItems = getCommentItems(processInstance.getId());
        List<ApprovalItem> ccItems = getCcItems(processInstance.getId());
//        List<ApprovalItem> forwardItems = getForwardItems(processInstance.getId());

        List<ApprovalItem> items = new ArrayList<>();
        items.add(startItem);
        items.addAll(approvalItems);
        items.addAll(commentItems);
        items.addAll(ccItems);
//        items.addAll(forwardItems);

        // 先排序，然后添加左右一个节点
        items.sort(Comparator.comparing(ApprovalItem::getOrderTime));

        if (processInstance.getStatus() != Running) {
            ApprovalItem item = ApprovalItem.builder()
                    .title("结束")
                    .timeInfo(processInstance.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .subTitle(processInstance.getStatus().statusName())
                    .type(ApprovalItem.Type.End)
                    .orderTime(processInstance.getEndTime())
                    .build();

            items.add(item);
        } else {
            Task task = fastORM.fetchable(Task.class)
                    .where()
                    .andEq(Task::getProcessInstanceId, processInstance.getId())
                    .andEq(Task::getStatus, Running)
                    .ret()
                    .fetch()
                    .orElseThrow();

            String currentApproval = task.getCurrTaskExecutors();
            String subTitle = currentApproval;
            int count = currentApproval.split(",").length;
            if (count > 1) {
                String currentApprovalDesc = switch (task.getSignRule()) {
                    case AND -> String.format("(%d人会签中)", count);
                    case OR -> String.format("(%d人或签中)", count);
                    case ByOrder -> String.format("(%d人依次审批中)", count);
                };
                subTitle += currentApprovalDesc;
            }

            List<IdName> personList = fastORM.queryable(TaskItem.class)
                    .where()
                    .andEq(TaskItem::getTaskId, task.getId())
                    .ret()
                    .query()
                    .stream()
                    .map(taskItem -> IdName.of(taskItem.getExecutorId(), taskItem.getExecutorName()))
                    .toList();

            ApprovalItem item = ApprovalItem.builder()
                    .title(subTitle)
                    .executors(currentApproval)
                    .personList(personList)
                    .timeInfo(task.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .subTitle(subTitle)
                    .type(ApprovalItem.Type.Pending)
                    .orderTime(task.getStartTime())
                    .build();

            items.add(item);
        }

        return items;
    }

    private ApprovalItem getStartItem(ProcessInstance processInstance) {
        return ApprovalItem.builder()
                .title("发起审批")
                .executors(processInstance.getPromoterName())
                .personList(List.of(IdName.of(processInstance.getPromoterId(), processInstance.getPromoterName())))
                .timeInfo(processInstance.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .type(ApprovalItem.Type.Start)
                .orderTime(processInstance.getStartTime())
                .build();
    }

    /**
     * 获取抄送信息
     *
     * @param processInstanceId 流程Id
     * @return 审批信息
     */
    private List<ApprovalItem> getCcItems(Long processInstanceId) {
        List<ApprovalItem> items = new ArrayList<>();
        List<CcItem> ccItemList = fastORM.queryable(CcItem.class)
                .where()
                .andEq(CcItem::getProcessInstanceId, processInstanceId)
                .ret().
                query();

        Map<Long, List<CcItem>> groupedItemList = ccItemList.stream()
                .collect(Collectors.groupingBy(CcItem::getTaskId));
        groupedItemList.forEach((taskId, itemList) -> {
            CcItem item0 = itemList.get(0);

            String executors = itemList.stream()
                    .map(CcItem::getToUserName)
                    .collect(Collectors.joining(","));
            List<IdName> personList = itemList.stream()
                    .map(item -> IdName.of(item.getToUserId(), item.getToUserName()))
                    .toList();

            ApprovalItem item = ApprovalItem.builder()
                    .title(item0.getTitle())
                    .executors(executors)
                    .personList(personList)
                    .timeInfo(item0.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .subTitle("已抄送" + itemList.size() + "人(" + executors + ")")
                    .orderTime(item0.getCreateAt())
                    .type(ApprovalItem.Type.CC)
                    .build();
            items.add(item);
        });

        return items;
    }

    /**
     * 获取审批信息
     *
     * @param processInstanceId 流程Id
     * @return 审批信息
     */
    private List<ApprovalItem> getApprovalItems(Long processInstanceId) {
        List<ApprovalItem> items = new ArrayList<>();
        List<TaskItemWithResult> taskItemList = fastORM.queryable(TaskItemWithResult.class)
                .where()
                .andEq(TaskItemWithResult::getProcessInstanceId, processInstanceId)
                .ret()
                .query();


        for (TaskItemWithResult taskItem : taskItemList) {
            String subTitle;
            ApprovalItem.Type type;
            switch (taskItem.getStatus()) {
                case Approved:
                    subTitle = "审批通过";
                    type = ApprovalItem.Type.Approved;
                    break;

                case Rejected:
                    subTitle = "审批不通过";
                    type = ApprovalItem.Type.Rejected;
                    break;

                default:
                    continue;
            }

            // 审批
            ApprovalItem item = ApprovalItem.builder()
                    .title(taskItem.getTitle())
                    .subTitle(subTitle)
                    .executors(taskItem.getExecutorName())
                    .timeInfo(taskItem.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .opinion(taskItem.getResult())
                    .type(type)
                    .orderTime(taskItem.getEndTime())
                    .build();
            items.add(item);
        }
        return items;
    }

    /**
     * 获取评论项
     *
     * @param processInstanceId 流程Guid
     * @return 评论项
     */
    private List<ApprovalItem> getForwardItems(Long processInstanceId) {
        List<ForwardInfoEntity> forwardItems =
                fastORM.queryable(ForwardInfoEntity.class)
                        .where()
                        .andEq(ForwardInfoEntity::getProcessInstanceId, processInstanceId)
                        .ret()
                        .query();

        List<ApprovalItem> items = new ArrayList<>();
        forwardItems.forEach(elem -> {
            // 转发信息
            ApprovalItem item = ApprovalItem.builder()
                    .executors(elem.getFromEmpName())
                    .timeInfo(elem.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .subTitle(String.format("已转发(%s)", elem.getComments()))
                    .opinion(elem.getInfo())
                    .type(ApprovalItem.Type.Forward)
                    .orderTime(elem.getCreatedAt())
                    .build();

            if (BooleanUtils.toBoolean(elem.getIsApproval())) {
                item.setTitle("审批人");
            } else {
                item.setTitle("评论人");
            }
            items.add(item);
        });

        return items;
    }

    /**
     * 获取评论项
     *
     * @param processInstanceId 流程Guid
     * @return 评论项
     */
    private List<ApprovalItem> getCommentItems(Long processInstanceId) {
        List<CommentItem> commentItems = fastORM.queryable(CommentItem.class)
                .where()
                .andEq(CommentItem::getProcessInstanceId, processInstanceId)
                .ret()
                .query();

        List<ApprovalItem> items = new ArrayList<>();
        commentItems.forEach(elem -> {
            // 转发信息
            ApprovalItem item = ApprovalItem.builder()
                    .title("评论")
                    .executors(elem.getCommentName())
                    .timeInfo(elem.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .subTitle("添加了评论")
                    .opinion(elem.getContent())
                    .type(ApprovalItem.Type.Comment)
                    .orderTime(elem.getCreatedAt())
                    .build();
            items.add(item);
        });

        return items;
    }

    /**
     * 获取流程附件
     *
     * @param processInstanceId 流程实例id
     * @return 附件信息
     */
    private List<IdName> getAttachments(Long processInstanceId) {
        List<ProcessInstanceAttachment> attachmentList = fastORM.queryable(ProcessInstanceAttachment.class)
                .where()
                .andEq(ProcessInstanceAttachment::getProcessInstanceId, processInstanceId)
                .ret()
                .query();

        return attachmentList.stream()
                .map(elem -> IdName.of(elem.getId(), elem.getName()))
                .toList();
    }

    /**
     * 结束进程
     *
     * @param processInstance 流程实例
     */
    public void completeProcessInstance(ProcessInstance processInstance) {
        log.info("结束流程:{}", processInstance.getStatus());

        businessService.afterWorkflowComplete(processInstance);
    }
}
