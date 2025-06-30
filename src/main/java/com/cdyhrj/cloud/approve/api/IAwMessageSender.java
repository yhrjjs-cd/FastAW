package com.cdyhrj.cloud.approve.api;

import java.util.List;

public interface IAwMessageSender {
    /**
     * 新增消息
     *
     * @param userId         用户Id
     * @param title          分类标题
     * @param subTitle       子标题
     * @param approveMessage 审批消息
     */
    void addMessage(Long userId,
                    String title,
                    String subTitle,
                    AwApproveMessage approveMessage);

    /**
     * 新增消息
     *
     * @param userIds        多个用户Id
     * @param title          分类标题
     * @param subTitle       子标题
     * @param approveMessage 审批消息
     */
    void addMessage(List<Long> userIds,
                    String title,
                    String subTitle,
                    AwApproveMessage approveMessage);

    /**
     * 新增消息
     *
     * @param userId         用户Id
     * @param title          消息体
     * @param approveMessage 审批消息
     * @param usePush        是否推送
     * @param pushToName     推送用户名称
     * @param isComplete     是否完成
     */
    void addMessage(Long userId,
                    String title,
                    AwApproveMessage approveMessage,
                    boolean usePush,
                    String pushToName,
                    boolean isComplete);
}
