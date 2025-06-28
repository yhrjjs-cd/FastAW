package com.cdyhrj.cloud.approve.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务审批参数
 *
 * @author 黄奇
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskApproveObject {
    /**
     * 任务单Id
     */
    @NotNull(message = "任务单Id不能为空")
    private Long taskItemId;

    /**
     * 处理人Id
     */
    @NotNull(message = "处理人Id不能为空")
    private Long userId;

    /**
     * 处理意见
     */
    private String opinion;
}
