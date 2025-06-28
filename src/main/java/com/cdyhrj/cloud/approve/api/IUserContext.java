package com.cdyhrj.cloud.approve.api;

/**
 * 用户上下文，系统需要实现这个接口，并注册
 */
public interface IUserContext {
    Long getTenantId();

    Long getDeptId();

    Long getUserId();

    String getUserName();
}
