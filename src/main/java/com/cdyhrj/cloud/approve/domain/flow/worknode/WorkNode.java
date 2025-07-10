package com.cdyhrj.cloud.approve.domain.flow.worknode;

import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 工作节点
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class WorkNode implements Serializable {
    /**
     * 节点id
     */
    private String id;

    /**
     * 节点类型
     */
    private NodeType nodeType;

    /**
     * 节点参数,不同的组件Id对应不同的参数类型，由前端定义
     */
    private Arg arg;

    /**
     * 下一个节点
     */
    private WorkNode next;

    /**
     * 把步骤信息写入输出列表
     *
     * @param steps     输出步骤列表
     * @param dataValue 提交的数据
     */
    public void writeStepsTo(IAwUserContext userContext, List<Step> steps, Map<String, Object> dataValue) {
        arg.writeStepsTo(userContext, steps, dataValue, this);
    }
}
