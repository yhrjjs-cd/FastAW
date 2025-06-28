package com.cdyhrj.cloud.approve.exception;

import com.cdyhrj.cloud.approve.enums.TaskStatus;

/**
 * 错误的状态异常
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public class WrongStatusException extends RuntimeException {
    public WrongStatusException(TaskStatus required, TaskStatus actual) {
        super(String.format("状态异常，期望状态为%s，实际状态为%s", required.statusName(), actual.statusName()));
    }
}
