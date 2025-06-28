package com.cdyhrj.cloud.approve.domain.flow.enums;

import java.util.Objects;

/**
 * 会签规则
 *
 * @author 黄奇
 */
public enum SignRule {
    /**
     * 或签
     */
    OR {
        @Override
        public String ruleName() {
            return "或签";
        }
    },
    /**
     * 会签
     */
    AND {
        @Override
        public String ruleName() {
            return "会签";
        }
    },
    /**
     * 依次审批
     */
    ByOrder {
        @Override
        public String ruleName() {
            return "依次审批";
        }
    };

    /**
     * @return 规则名称
     */
    public abstract String ruleName();

    /**
     * 获取会签规则, 默认发货One
     *
     * @param multiSelectType 多选类型
     * @return 会签规则
     */
    public static SignRule from(String multiSelectType) {
        Objects.requireNonNull(multiSelectType, "获取会签规则不能为空");

        return SignRule.valueOf(multiSelectType);
    }
}
