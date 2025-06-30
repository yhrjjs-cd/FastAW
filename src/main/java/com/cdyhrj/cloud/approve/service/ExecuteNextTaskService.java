package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.api.AwApproveMessage;
import com.cdyhrj.cloud.approve.api.IAwMessageSender;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SignRule;
import com.cdyhrj.cloud.approve.entity.CcItem;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.entity.ProcessInstanceBizRelation;
import com.cdyhrj.cloud.approve.entity.ProcessInstanceWf;
import com.cdyhrj.cloud.approve.entity.Task;
import com.cdyhrj.cloud.approve.entity.TaskItem;
import com.cdyhrj.cloud.approve.entity.Variable;
import com.cdyhrj.cloud.approve.enums.EmptyApprovalRule;
import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.cloud.approve.exception.NoApprovalPersonException;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行下一个任务服务
 * 黄奇(huangqi@itek-china.com)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExecuteNextTaskService {
    private final FastORM fastORM;
    private final BusinessService businessService;
    private final IAwUserContext userContext;
    private final IAwMessageSender messageService;
    public static final String MESSAGE_TITLE_TEMPLATE = "%s发起的审批";

    /**
     * 执行下一个任务
     */
    public void execNextTask(ProcessInstance processInstance) {
        // 查找最Index最小，并且未初始化的一个Task，创建工单
        Optional<Task> task = fastORM.fetchable(Task.class)
                .where()
                .andEq(Task::getStatus, TaskStatus.Created)
                .andEq(Task::getProcessInstanceId, processInstance.getId())
                .ret()
                .orderBy()
                .ret()
                .fetch();

        task.ifPresentOrElse(t -> {
            t.setStartTime(LocalDateTime.now());
            t.setStatus(TaskStatus.Running);

            // 创建所有工单
            ProcessInstanceWf processInstanceWf = getProcessInstanceWf(processInstance.getId());

            Step step = processInstanceWf.getRuntimeWf()
                    .getSteps()
                    .get(t.getOriginTaskIndex() - 1);

            if (step.getNodeType() == NodeType.CC) {
                executeWithCc(processInstance, step, t);
            } else if (step.getNodeType() == NodeType.APPROVE) {
                // 审批
                if (Objects.isNull(step.getPersonList()) ||
                        step.getPersonList()
                                .isEmpty()) {
                    this.execWithEmptyApprovalTask(processInstance, t, step);

                    return;
                }

                if (step.getSignRule() == SignRule.ByOrder) {
                    execByOrderTask(processInstance, t, step);
                } else {
                    execOneOrAllTask(processInstance, t, step);
                }
            }
        }, () -> {
            end(processInstance);
        });
    }

    /**
     * 执行抄送
     *
     * @param processInstance 流程实例
     * @param step            步骤
     * @param task            任务
     */
    private void executeWithCc(ProcessInstance processInstance, Step step, Task task) {
        // 抄送
        String taskStatus = getVariable(processInstance, Variable.KEY_RESULT);
        if (StringUtils.isBlank(taskStatus)) {
            executeWithCcAfterStartNode(processInstance, step, task);
        } else {
            executeWithCcAfterApproveNode(processInstance, step, task, taskStatus);
        }
    }

    private void executeWithCcAfterStartNode(ProcessInstance processInstance, Step step, Task task) {
        String subTitle = MESSAGE_TITLE_TEMPLATE.formatted(processInstance.getPromoterName());

        long tenantId = userContext.getTenantId();
        List<CcItem> ccItemList = step.getPersonList().stream().map(person ->
                CcItem.builder()
                        .processInstanceId(processInstance.getId())
                        .taskId(task.getId())
                        .title(task.getTitle())
                        .subTitle(subTitle)
                        .toUserId(person.getId())
                        .toUserName(person.getName())
                        .createAt(LocalDateTime.now())
                        .tenantId(tenantId)
                        .build()
        ).toList();

        task.setEndTime(LocalDateTime.now());
        task.setStatus(TaskStatus.Approved); //抄送任务自动通过

        fastORM.updatable(task).update();
        fastORM.insertable(ccItemList).insert();

        // 执行下一个任务
        this.execNextTask(processInstance);

        ccItemList.forEach(ccItem -> messageService.addMessage(
                ccItem.getToUserId(),
                MESSAGE_TITLE_TEMPLATE.formatted(processInstance.getPromoterName()),
                AwApproveMessage.of(
                        "",
                        processInstance.getBizType(),
                        processInstance.getBizId(),
                        processInstance.getId(),
                        ""),
                false,
                null,
                false));
    }

    /**
     * 从审批节点后开始抄送
     *
     * @param processInstance 流程实例
     * @param step            步骤
     * @param task            任务
     * @param taskStatus      任务状态
     */
    private void executeWithCcAfterApproveNode(ProcessInstance processInstance, Step step, Task task, String taskStatus) {
        long taskId = Long.parseLong(getVariable(processInstance, Variable.KEY_PREV_TASK_ID));

        TaskStatus status = TaskStatus.valueOf(taskStatus);
        String subTitle = getSubTitle(taskId);

        long tenantId = userContext.getTenantId();
        List<CcItem> ccItemList = step.getPersonList().stream().map(person ->
                CcItem.builder()
                        .processInstanceId(processInstance.getId())
                        .taskId(task.getId())
                        .title(task.getTitle())
                        .subTitle(subTitle)
                        .toUserId(person.getId())
                        .toUserName(person.getName())
                        .status(status)
                        .createAt(LocalDateTime.now())
                        .tenantId(tenantId)
                        .build()
        ).toList();

        task.setEndTime(LocalDateTime.now());
        task.setStatus(TaskStatus.Approved); //抄送任务自动通过

        fastORM.updatable(task).update();
        fastORM.insertable(ccItemList).insert();

        // 执行下一个任务
        this.execNextTask(processInstance);

        ccItemList.forEach(ccItem -> messageService.addMessage(
                ccItem.getToUserId(),
                MESSAGE_TITLE_TEMPLATE.formatted(processInstance.getPromoterName()),
                AwApproveMessage.of(
                        "",
                        processInstance.getBizType(),
                        processInstance.getBizId(),
                        processInstance.getId(),
                        ""),
                false,
                null,
                false));
    }

    private String getSubTitle(long taskId) {
        List<TaskItem> taskItemList = fastORM.queryable(TaskItem.class)
                .where()
                .andEq(TaskItem::getTaskId, taskId)
                .andOrGroup()
                .orEq(TaskItem::getStatus, TaskStatus.Approved)
                .orEq(TaskItem::getStatus, TaskStatus.Rejected)
                .end()
                .ret()
                .orderBy().add(TaskItem::getItemIndex).ret()
                .query();

        return taskItemList.stream()
                .map(taskItem -> taskItem.getExecutorName() + "(" + taskItem.getStatus().statusName() + ")")
                .reduce((a, b) -> a + "," + b)
                .orElse("-");
    }

    private void execByOrderTask(ProcessInstance processInstance, Task task, Step step) {
        long tenantId = userContext.getTenantId();
        //第一个置为运行中
        AtomicInteger index = new AtomicInteger(1);
        List<TaskItem> taskItemList = step.getPersonList()
                .stream()
                .map(person -> {
                    TaskItem taskItem = TaskItem.builder()
                            .taskId(task.getId())
                            .title(task.getTitle())
                            .processInstanceId(processInstance.getId())
                            .executorId(person.getId())
                            .executorName(person.getName())
                            .startTime(new Date())
                            .status(TaskStatus.Created)
                            .itemIndex(index.getAndIncrement())
                            .build();
                    taskItem.setTenantId(tenantId);

                    return taskItem;
                }).toList();

        TaskItem taskItem0 = taskItemList.get(0);
        taskItem0.setStatus(TaskStatus.Running);

        fastORM.updatable(task).update();
        fastORM.insertable(taskItemList).insert();
        fastORM.updatable(ProcessInstance.class)
                .id(processInstance.getId())
                .updateField(
                        ProcessInstance::getCurrTask, task.getTitle(),
                        ProcessInstance::getCurrTaskExecutors, task.getCurrTaskExecutors()
                );
        fastORM.updatable(ProcessInstanceBizRelation.class)
                .where()
                .ret()
                .updateField(ProcessInstanceBizRelation::getProcessInfo, StepUtils.extractStepTipInfo(step));

//        messageService.addMessage(
//                taskItem0.getExecutorId(),
//                MESSAGE_TITLE_TEMPLATE.formatted(processInstance.getPromoterName()),
//                ApproveMessage.of(
//                        "",
//                        processInstance.getBizType(),
//                        processInstance.getBizId(),
//                        processInstance.getId(),
//                        taskItem0.getId(),
//                        ""),
//                true,
//                processInstance.getPromoterName(),
//                false,
//                NoticeType.APPROVE);
    }

    /**
     * @param processInstance 流程实例
     * @param task            任务实例
     * @param step            步骤
     */
    private void execOneOrAllTask(ProcessInstance processInstance, Task task, Step step) {
        long tenantId = userContext.getTenantId();
        // 全部置为运行中
        AtomicInteger index = new AtomicInteger(1);
        List<TaskItem> taskItemList = step.getPersonList()
                .stream()
                .map(person -> {
                    TaskItem taskItem = TaskItem.builder()
                            .taskId(task.getId())
                            .title(task.getTitle())
                            .processInstanceId(processInstance.getId())
                            .executorId(person.getId())
                            .executorName(person.getName())
                            .startTime(new Date())
                            .status(TaskStatus.Running)
                            .itemIndex(index.getAndIncrement())
                            .build();
                    taskItem.setTenantId(tenantId);
                    return taskItem;
                }).toList();

        fastORM.updatable(task).update();
        fastORM.insertable(taskItemList).insert();
        fastORM.updatable(ProcessInstance.class)
                .id(processInstance.getId())
                .updateField(
                        ProcessInstance::getCurrTask, task.getTitle(),
                        ProcessInstance::getCurrTaskExecutors, task.getCurrTaskExecutors());
        fastORM.updatable(ProcessInstanceBizRelation.class)
                .where()
                .ret()
                .updateField(ProcessInstanceBizRelation::getProcessInfo, StepUtils.extractStepTipInfo(step));

//        taskItemList.forEach(taskItem ->
//                messageService.addMessage(
//                        taskItem.getExecutorId(),
//                        MESSAGE_TITLE_TEMPLATE.formatted(processInstance.getPromoterName()),
//                        ApproveMessage.of(
//                                "",
//                                processInstance.getBizType(),
//                                processInstance.getBizId(),
//                                processInstance.getId(),
//                                taskItem.getId(),
//                                ""),
//                        true,
//                        processInstance.getPromoterName(),
//                        false,
//                        NoticeType.APPROVE
//                )
//        );
    }

    /**
     * 执行步骤审批人为空，根据EmptyApprovalRule逻辑处理
     *
     * @param processInstance 引擎服务
     * @param task            任务
     * @param step            步骤
     */
    private void execWithEmptyApprovalTask(ProcessInstance processInstance, Task task, Step step) {
        if (step.getEmptyApprovalRule() != EmptyApprovalRule.AutoPass) {
            throw new NoApprovalPersonException(step.getName());
        }

        Date current = new Date();
        TaskItem taskItem = TaskItem.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .processInstanceId(processInstance.getId())
                .executorId(null)
                .executorName("系统")
                .startTime(current)
                .endTime(current)
                .status(TaskStatus.Approved)
                .itemIndex(1)
                .opinion("无审批人，自动审批通过")
                .build();
        taskItem.setTenantId(userContext.getTenantId());
        fastORM.insertable(taskItem).insert();

        task.setSignedNum(1);
        task.setEndTime(LocalDateTime.now());
        task.setStatus(TaskStatus.Approved);

        fastORM.updatable(task).update();

        setVariable(processInstance, Variable.KEY_RESULT, TaskStatus.Approved.toString());
        setVariable(processInstance, Variable.KEY_PREV_TASK_ID, task.getId().toString());

        this.execNextTask(processInstance);
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
                .ifPresentOrElse(t -> {
                    t.setValue(value);
                    fastORM.updatable(t).update();
                }, () -> {
                    Variable variable = new Variable();
                    variable.setName(key);
                    variable.setValue(value);
                    variable.setProcessInstanceId(processInstance.getId());

                    fastORM.insertable(variable).insert();
                });
    }

    /**
     * 获取流程实例对应的流程配置
     *
     * @param processInstanceId 流程实例Id
     * @return 流程配置
     */
    public ProcessInstanceWf getProcessInstanceWf(Long processInstanceId) {
        return fastORM.fetchable(ProcessInstanceWf.class)
                .where()
                .andEq(ProcessInstanceWf::getProcessInstanceId, processInstanceId)
                .ret()
                .fetch()
                .orElseThrow(() -> new NullPointerException("处理出错：流程配置为空, 实例Id: + " + processInstanceId));
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
                            ProcessInstance::getStatus, processInstance.getStatus());

            fastORM.updatable(ProcessInstanceBizRelation.class)
                    .where()
                    .andEq(ProcessInstanceBizRelation::getProcessInstanceId, processInstance.getId())
                    .ret()
                    .updateField(
                            ProcessInstanceBizRelation::getProcessInfo, "已通过",
                            ProcessInstanceBizRelation::getProcessStatus, ProcessInstanceStatus.Finished);
        } else if (status == TaskStatus.Rejected) {
            processInstance.setStatus(ProcessInstanceStatus.Failure);
            fastORM.updatable(ProcessInstance.class)
                    .id(processInstance.getId())
                    .updateField(
                            ProcessInstance::getEndTime, processInstance.getEndTime(),
                            ProcessInstance::getCurrTask, null,
                            ProcessInstance::getCurrTaskExecutors, null,
                            ProcessInstance::getStatus, processInstance.getStatus());

            fastORM.updatable(ProcessInstanceBizRelation.class)
                    .where()
                    .andEq(ProcessInstanceBizRelation::getProcessInstanceId, processInstance.getId())
                    .ret()
                    .updateField(
                            ProcessInstanceBizRelation::getProcessInfo, "未通过",
                            ProcessInstanceBizRelation::getProcessStatus, ProcessInstanceStatus.Failure);
        } else {
            // TODO 输出异常
        }

        this.completeProcessInstance(processInstance);
    }


    /**
     * 结束进程
     *
     * @param processInstance 流程实例
     */
    public void completeProcessInstance(ProcessInstance processInstance) {
        businessService.afterWorkflowComplete(processInstance);
    }
}