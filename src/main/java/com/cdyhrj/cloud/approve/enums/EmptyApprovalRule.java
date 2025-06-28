package com.cdyhrj.cloud.approve.enums;

/**
 * 审批人为空时规则
 *
 * @author 黄奇
 */

public enum EmptyApprovalRule {
    /**
     * 自动通过
     */
    AutoPass,

    /**
     * 自动转交管理员
     */
    SendToAdmin
}