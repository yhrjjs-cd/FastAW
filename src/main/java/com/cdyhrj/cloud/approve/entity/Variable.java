package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Id;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.Entity;
import lombok.Data;

/**
 * 流程变量
 *
 * @author 黄奇
 */
@Data
@Table(name = "wf_variable")
public class Variable implements Entity {
    /**
     * 结果变量
     */
    public static final String KEY_RESULT = "Result";

    /**
     * 上一审批节点，抄送节点需要
     */
    public static final String KEY_PREV_TASK_ID = "PrevTaskId";

    /**
     * 主键Guid
     */
    @Column
    @Id
    private Long id;

    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;
    /**
     * 参数名称
     */
    @Column
    private String name;

    /**
     * 参数值
     */
    @Column
    private String value;
}
