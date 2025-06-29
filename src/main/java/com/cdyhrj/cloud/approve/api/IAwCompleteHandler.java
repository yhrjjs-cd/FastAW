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
     * @param bizId     业务Id
     * @param isSuccess 是否成功
     */
    void complete(Long bizId, boolean isSuccess);
}
