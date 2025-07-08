package com.cdyhrj.cloud.approve.api;

/**
 * 流程完成处理器
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public interface IAwCompleteHandler {
    /**
     * 执行完成
     *
     * @param bizType      业务类型
     * @param tenantId     企业Id
     * @param bizId        业务Id
     * @param promoterId   发起人Id
     * @param promoterName 发起人姓名
     * @param isSuccess    是否成功
     */
    void complete(String bizType, Long tenantId, Long bizId, Long promoterId, String promoterName, boolean isSuccess);
}
