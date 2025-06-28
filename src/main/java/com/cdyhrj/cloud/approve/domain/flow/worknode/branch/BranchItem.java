package com.cdyhrj.cloud.approve.domain.flow.worknode.branch;

import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class BranchItem implements Serializable {
    private WorkNode node;

    /**
     * 条件
     */
    private Condition[] conditions;

    /**
     * 条件连接方法
     */
    private LinkMethod linkMethod;
}
