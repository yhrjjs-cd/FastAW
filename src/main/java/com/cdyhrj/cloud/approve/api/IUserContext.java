package com.cdyhrj.cloud.approve.api;

/**
 * 用户上下文，系统需要实现这个接口，并注册
 */
public interface IUserContext {
    /**
     * 获取租户id, 用于多租户系统，不用可以默认0
     *
     * @return 租户Id
     */
    Long getTenantId();

    /**
     * @return 部门Id
     */
    Long getDeptId();

    /**
     * @return 用户Id
     */
    Long getUserId();

    /**
     * @return 获取用户名称
     */
    String getUserName();
}
