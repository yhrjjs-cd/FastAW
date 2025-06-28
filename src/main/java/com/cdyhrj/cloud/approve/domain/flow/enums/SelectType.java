package com.cdyhrj.cloud.approve.domain.flow.enums;

import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.DirectorSuperiorArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.ScriptExpressionArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.SelectDeptAndRoleArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.SelectPersonArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.SelectRoleArg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.approve.SelectTypeArg;

/**
 * 选择类型
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public enum SelectType {
    /**
     * 指定人员
     */
    SelectPerson {
        @Override
        public Class<? extends SelectTypeArg> getTypeArgClass() {
            return SelectPersonArg.class;
        }
    },

    /**
     * 直接上级
     */
    DirectorSuperior {
        @Override
        public Class<? extends SelectTypeArg> getTypeArgClass() {
            return DirectorSuperiorArg.class;
        }
    },

    /**
     * 脚本表达式
     */
    ScriptExpression {
        @Override
        public Class<? extends SelectTypeArg> getTypeArgClass() {
            return ScriptExpressionArg.class;
        }
    },

    /**
     * 选择角色
     */
    SelectRole {
        @Override
        public Class<? extends SelectTypeArg> getTypeArgClass() {
            return SelectRoleArg.class;
        }
    },

    /**
     * 选择部门角色
     */
    SelectDeptAndRole {
        @Override
        public Class<? extends SelectTypeArg> getTypeArgClass() {
            return SelectDeptAndRoleArg.class;
        }
    };

    public abstract Class<? extends SelectTypeArg> getTypeArgClass();
}
