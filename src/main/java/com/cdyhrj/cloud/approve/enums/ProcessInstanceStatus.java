package com.cdyhrj.cloud.approve.enums;

/**
 * 流程状态
 *
 * @author 黄奇
 */
public enum ProcessInstanceStatus {
    /**
     * 审批中
     */
    Running {
        @Override
        public String statusName() {
            return "审批中";
        }
    },
    /**
     * 已完成
     */
    Finished {
        @Override
        public String statusName() {
            return "审批通过";
        }
    },
    /**
     * 已失败
     */
    Failure {
        @Override
        public String statusName() {
            return "审批不通过";
        }
    },
    /**
     * 已撤销
     */
    Canceled {
        @Override
        public String statusName() {
            return "已撤销";
        }
    };

    /**
     * @return 状态名称
     */
    public abstract String statusName();
}
