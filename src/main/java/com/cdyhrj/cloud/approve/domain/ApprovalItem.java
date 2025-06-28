package com.cdyhrj.cloud.approve.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批项
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
public class ApprovalItem {
    /**
     * 步骤标题
     */
    private String title;

    /**
     * 处理人
     */
    private String executors;

    /**
     * 时间信息
     */
    private String timeInfo;

    /**
     * 子信息
     */
    private String subTitle;

    /**
     * 审批意见
     */
    private String opinion;

    /**
     * 类型
     */
    private Type type;

    /**
     * 相关处理人
     */
    private List<IdName> personList;
    /**
     * 排序时间
     */
    @JSONField(serialize = false)
    private LocalDateTime orderTime;

    /**
     * 类型
     */
    public enum Type {
        /**
         * 发起节点
         */
        Start,
        /**
         * 审批通过
         */
        Approved,

        /**
         * 审批不通过
         */
        Rejected,

        /**
         * 审批中
         */
        Pending,

        /**
         * 抄送
         */
        CC,

        /**
         * 评论
         */
        Comment,

        /**
         * 转发
         */
        Forward,
        /**
         * 审批结束
         */
        End
    }
}
