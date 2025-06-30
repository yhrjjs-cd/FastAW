package com.cdyhrj.cloud.approve.api;

import lombok.Data;

/**
 * 审批消息
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class AwApproveMessage {
    public static AwApproveMessage of(String title, String bizType, Long bizId, Long processInstanceId, String taskType) {
        AwApproveMessage approveMessage = new AwApproveMessage();
        approveMessage.setTitle(title);
        approveMessage.setBizType(bizType);
        approveMessage.setBizId(bizId);
        approveMessage.setProcessInstanceId(processInstanceId);
        approveMessage.setTaskType(taskType);

        return approveMessage;
    }

    public static AwApproveMessage of(String title, String bizType, Long bizId, Long processInstanceId, Long taskItemId, String taskType) {
        AwApproveMessage approveMessage = of(title, bizType, bizId, processInstanceId, taskType);

        approveMessage.setTaskItemId(taskItemId);

        return approveMessage;
    }

    // 标题
    private String title;

    // 类型
    private String bizType;

    // 业务ID，对应不同的意思
    private Long bizId;

    // 流程引擎Id，只有流程有效
    private Long processInstanceId;

    // 任务Id
    private Long taskItemId;

    // 工单类型，任务工单有效
    private String taskType;
}
