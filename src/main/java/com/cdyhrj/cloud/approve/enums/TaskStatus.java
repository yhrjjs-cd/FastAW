package com.cdyhrj.cloud.approve.enums;

import java.util.function.Predicate;

/**
 * 任务（任务单状态）状态
 *
 * @author 黄奇
 */
public enum TaskStatus implements Predicate<String> {
    /**
     * 待审批
     */
    Created {
        @Override
        public String statusName() {
            return "待审批";
        }

        @Override
        public boolean test(String s) {
            return "Created".equals(s);
        }
    },
    /**
     * 审批中
     */
    Running {
        @Override
        public String statusName() {
            return "审批中";
        }

        @Override
        public boolean test(String s) {
            return "Running".equals(s);
        }
    },
    /**
     * 已同意
     */
    Approved {
        @Override
        public String statusName() {
            return "审批通过";
        }

        @Override
        public boolean test(String s) {
            return "Approved".equals(s);
        }
    },
    /**
     * 已拒绝
     */
    Rejected {
        @Override
        public String statusName() {
            return "审批拒绝";
        }

        @Override
        public boolean test(String s) {
            return "Rejected".equals(s);
        }
    },
    /**
     * 撤销
     */
    Canceled {
        @Override
        public String statusName() {
            return "撤销";
        }

        @Override
        public boolean test(String s) {
            return "Canceled".equals(s);
        }
    };

    /**
     * @return 状态名
     */
    public abstract String statusName();
}
