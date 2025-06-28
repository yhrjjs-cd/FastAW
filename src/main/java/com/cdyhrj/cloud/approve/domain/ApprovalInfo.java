package com.cdyhrj.cloud.approve.domain;

import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批信息
 * (iTek-china 2022)
 *
 * @author <a href="huangqi@itek-china.com">黄奇</a>
 * @version 7.0
 * <pre>
 *   2022-10-20 * 黄奇创建
 * </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalInfo {
    /**
     * 发起人
     */
    private String promoter;

    /**
     * 发起人Guid
     */
    private Long promoterId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 附件
     */
    private List<IdName> attachments;

    /**
     * 审批项
     */
    private List<ApprovalItem> items;

    /**
     * 流程状态
     */
    private ProcessInstanceStatus status;
}
