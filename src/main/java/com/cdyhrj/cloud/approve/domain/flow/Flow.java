package com.cdyhrj.cloud.approve.domain.flow;

import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程配置信息
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class Flow implements Serializable {
    /**
     * 流程标题
     */
    private String title;

    /**
     * 流程开始节点
     */
    private StartNode startNode;

    /**
     * 工作节点
     */
    private WorkNode next;

    /**
     * 流程结束节点
     */
    private EndNode endNode;

    /**
     * 流程参数
     */
    private Param[] paramList;

    /**
     * 描述模版，用于计算描述
     */
    private String contentTemplate;


    /**
     * 是否需要计算， 如果需要计算，前端输入数据后，需要刷新重新获取
     * !这个字段设计器不设置
     *
     * @return 是否需要计算
     */
    public Boolean getNeedCalc() {
        return true;
    }
}
