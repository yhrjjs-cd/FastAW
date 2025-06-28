package com.cdyhrj.cloud.approve.exception;

/**
 * 无审批人错误
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public class NoApprovalPersonException extends RuntimeException {
    public NoApprovalPersonException(String name) {
        super("流程执行出错：无审批人(节点-" + name + ")");
    }
}
