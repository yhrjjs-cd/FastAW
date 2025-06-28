package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.domain.TaskApproveObject;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.entity.Task;
import com.cdyhrj.cloud.approve.entity.TaskItem;
import com.cdyhrj.cloud.approve.entity.TaskItemClob;
import com.cdyhrj.cloud.approve.entity.Variable;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.cloud.approve.exception.WrongStatusException;
import com.cdyhrj.cloud.message.domain.ApproveMessage;
import com.cdyhrj.cloud.message.domain.NoticeType;
import com.cdyhrj.cloud.message.service.MessageService;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Objects;

/**
 * 流程任务服务
 * 黄奇(huangqi@itek-china.com)
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    private final FastORM fastORM;
    private final ProcessInstanceService processInstanceService;
    private final ExecuteNextTaskService executeNextTaskService;
    private final MessageService messageService;

    /**
     * 执行工单
     *
     * @param taskApproveObject 工单内容
     * @param taskItemStatus    工单状态
     * @return 工单主键
     */
    @Transactional(rollbackFor = Exception.class)
    public Long execTaskItem(TaskApproveObject taskApproveObject, TaskStatus taskItemStatus) {
        // 查询工单
        TaskItem taskItem = fastORM.fetchable(TaskItem.class)
                .id(taskApproveObject.getTaskItemId())
                .fetch()
                .orElseThrow(() -> new IllegalArgumentException("审批项不存在:" + taskApproveObject.getTaskItemId()));

        Assert.isTrue(taskItem.getStatus() == TaskStatus.Running, "非待审批状态,审批项:" + taskApproveObject.getTaskItemId());
        Assert.isTrue(Objects.equals(taskItem.getExecutorId(), taskApproveObject.getUserId()), "审批人员不匹配");

        // 执行工单
        taskItem.setEndTime(new Date());
        taskItem.setStatus(taskItemStatus);
        String opinion = taskApproveObject.getOpinion();
        if (StringUtils.isNotBlank(opinion)) {
            if (opinion.length() > TaskItem.MAX_OPINION_FIELD_LENGTH) {
                TaskItemClob taskItemClob = new TaskItemClob(taskItem.getId(), opinion);
                fastORM.insertable(taskItemClob).insert();
            } else {
                taskItem.setOpinion(opinion);
            }
        }
        fastORM.updatable(taskItem).update();
        sendToPromoter(taskItem, taskItemStatus);

        this.checkTask(taskItem.getTaskId(), taskItemStatus);

        return taskItem.getId();
    }

    /**
     * 抄送给发起人
     *
     * @param taskItem       审批项
     * @param taskItemStatus 审批状态
     */
    private void sendToPromoter(TaskItem taskItem, TaskStatus taskItemStatus) {
        ProcessInstance processInstance = processInstanceService.fetchProcessInstance(taskItem.getProcessInstanceId());

        if (taskItemStatus == TaskStatus.Approved) {
            messageService.addMessage(
                    processInstance.getPromoterId(),
                    "你提交的`%s`审批已通过".formatted(taskItem.getExecutorName()),
                    ApproveMessage.of(
                            "",
                            processInstance.getBizType(),
                            processInstance.getBizId(),
                            processInstance.getId(),
                            ""
                    ),
                    true,
                    processInstance.getPromoterName(),
                    true,
                    NoticeType.APPROVE
            );
        } else {
            messageService.addMessage(
                    processInstance.getPromoterId(),
                    "你提交的`%s`审批未通过".formatted(taskItem.getExecutorName()),
                    ApproveMessage.of(
                            "",
                            processInstance.getBizType(),
                            processInstance.getBizId(),
                            processInstance.getId(),
                            ""
                    ),
                    true,
                    processInstance.getPromoterName(),
                    true,
                    NoticeType.APPROVE
            );
        }

    }

    /**
     * 检查任务
     *
     * @param taskId         任务Guid
     * @param taskItemStatus 任务状态
     */
    private void checkTask(Long taskId, TaskStatus taskItemStatus) {
        Task task = fastORM.fetchable(Task.class)
                .id(taskId)
                .fetch()
                .orElseThrow();

        if (task.getStatus() != TaskStatus.Running) {
            throw new WrongStatusException(TaskStatus.Running, task.getStatus());
        }

        switch (task.getSignRule()) {
            case OR:
                this.finishTaskWithOne(task, taskItemStatus);
                break;

            case AND:
                this.finishTaskWithAll(task, taskItemStatus);
                break;

            case ByOrder:
                this.finishTaskByOrder(task, taskItemStatus);
                break;
        }
    }

    /**
     * 或签处理
     *
     * @param task           任务
     * @param taskItemStatus 当前任务单状态
     */
    private void finishTaskWithOne(Task task, TaskStatus taskItemStatus) {
        task.setSignedNum(1);
        task.setEndTime(new Date());
        task.setStatus(taskItemStatus);
        fastORM.updatable(task)
                .update();

        ProcessInstance processInstance = processInstanceService.fetchProcessInstance(task.getProcessInstanceId());
        processInstanceService.setVariable(processInstance, Variable.KEY_RESULT, taskItemStatus.toString());
        processInstanceService.setVariable(processInstance, Variable.KEY_PREV_TASK_ID, task.getId().toString());

        if (TaskStatus.Rejected == taskItemStatus) {
            cancelRunningTask(processInstance, task);
            processInstanceService.end(processInstance);
        } else {
            cancelRunningTaskItems(task);
            executeNextTaskService.execNextTask(processInstance);
        }
    }

    /**
     * 取消运行的任务
     *
     * @param task 任务
     */
    private void cancelRunningTaskItems(Task task) {
        fastORM.updatable(TaskItem.class)
                .where()
                .andEq(TaskItem::getTaskId, task.getId())
                .andEq(TaskItem::getStatus, TaskStatus.Running)
                .ret()
                .updateField(
                        TaskItem::getStatus, TaskStatus.Canceled,
                        TaskItem::getEndTime, new Date()
                );
    }

    /**
     * 取消任务
     *
     * @param processInstance 流程实例
     * @param task            任务
     */
    private void cancelRunningTask(ProcessInstance processInstance, Task task) {
        // 1.取消所有待运行的任务
        // 2.取消所有运行创建的任务
        fastORM.updatable(TaskItem.class)
                .where()
                .andEq(TaskItem::getTaskId, task.getId())
                .andOrGroup()
                .orEq(TaskItem::getStatus, TaskStatus.Created)
                .orEq(TaskItem::getStatus, TaskStatus.Running)
                .end()
                .ret()
                .updateField(TaskItem::getStatus, TaskStatus.Canceled,
                        TaskItem::getEndTime, new Date());

        fastORM.updatable(Task.class)
                .where()
                .andEq(Task::getProcessInstanceId, processInstance.getId())
                .andOrGroup()
                .orEq(Task::getStatus, TaskStatus.Created)
                .orEq(Task::getStatus, TaskStatus.Running)
                .end()
                .ret();
    }

    /**
     * 会签处理
     *
     * @param task           任务
     * @param taskItemStatus 当前任务单状态
     */
    private void finishTaskWithAll(Task task, TaskStatus taskItemStatus) {
        int signedNum = task.getSignedNum() + 1;
        task.setSignedNum(signedNum);

        if (signedNum == task.getToSignNum()) {
            task.setEndTime(new Date());
            task.setStatus(taskItemStatus);
        }

        if (TaskStatus.Rejected == taskItemStatus) {
            task.setEndTime(new Date());
            task.setStatus(taskItemStatus);
        }

        fastORM.updatable(task).update();

        ProcessInstance processInstance = processInstanceService.fetchProcessInstance(task.getProcessInstanceId());
        processInstanceService.setVariable(processInstance, Variable.KEY_RESULT, taskItemStatus.toString());
        processInstanceService.setVariable(processInstance, Variable.KEY_PREV_TASK_ID, task.getId().toString());

        if (TaskStatus.Rejected == taskItemStatus) {
            cancelRunningTask(processInstance, task);

            processInstanceService.end(processInstance);
        } else {
            if (signedNum == task.getToSignNum()) {
                executeNextTaskService.execNextTask(processInstance);
            }
        }
    }

    /**
     * 按顺序处理
     *
     * @param task           任务
     * @param taskItemStatus 当前任务单状态
     */
    private void finishTaskByOrder(Task task, TaskStatus taskItemStatus) {
        try {
            int signedNum = task.getSignedNum() + 1;
            task.setSignedNum(signedNum);

            if (signedNum == task.getToSignNum()) {
                task.setEndTime(new Date());
                task.setStatus(taskItemStatus);
            }

            if (TaskStatus.Rejected == taskItemStatus) {
                task.setEndTime(new Date());
                task.setStatus(taskItemStatus);
            }

            fastORM.updatable(task).update();

            ProcessInstance processInstance = processInstanceService.fetchProcessInstance(task.getProcessInstanceId());
            processInstanceService.setVariable(processInstance, Variable.KEY_RESULT, taskItemStatus.toString());
            processInstanceService.setVariable(processInstance, Variable.KEY_PREV_TASK_ID, task.getId().toString());

            if (TaskStatus.Rejected == taskItemStatus) {
                cancelRunningTask(processInstance, task);

                processInstanceService.end(processInstance);
            } else {
                if (signedNum == task.getToSignNum()) {
                    executeNextTaskService.execNextTask(processInstance);
                } else {
                    // 处理设置下一个Item为处理中
                    // 查找最Index最小，并且未初始化的一个Task，创建工单
                    TaskItem taskItem = fastORM.fetchable(TaskItem.class)
                            .where()
                            .andEq(TaskItem::getStatus, TaskStatus.Created)
                            .andEq(TaskItem::getTaskId, task.getId())
                            .ret()
                            .orderBy().add(TaskItem::getItemIndex)
                            .ret()
                            .fetch()
                            .orElseThrow();

                    taskItem.setStatus(TaskStatus.Running);
                    fastORM.updatable(taskItem).update();

                    messageService.addMessage(
                            taskItem.getExecutorId(),
                            processInstance.getPromoterName() + "发起的审批",
                            ApproveMessage.of(
                                    "",
                                    processInstance.getBizType(),
                                    processInstance.getBizId(),
                                    processInstance.getId(),
                                    taskItem.getId(),
                                    ""),
                            true,
                            taskItem.getExecutorName(),
                            false,
                            NoticeType.APPROVE);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}