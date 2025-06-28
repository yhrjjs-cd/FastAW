package com.cdyhrj.cloud.approve.domain.flow.enums;

import com.cdyhrj.cloud.approve.domain.flow.worknode.Arg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.ApproveArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.branch.BranchArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.cc.CcArg;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public enum NodeType {
    APPROVE {
        @Override
        public Class<? extends Arg> getNodeClass() {
            return ApproveArg.class;
        }
    },
    CC {
        @Override
        public Class<? extends Arg> getNodeClass() {
            return CcArg.class;
        }
    },
    BRANCH {
        @Override
        public Class<? extends Arg> getNodeClass() {
            return BranchArg.class;
        }
    };

    public abstract Class<? extends Arg> getNodeClass();
}
