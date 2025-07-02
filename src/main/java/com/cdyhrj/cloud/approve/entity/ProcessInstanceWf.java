package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.domain.RuntimeWf;
import com.cdyhrj.fast.orm.adapter.ObjectValueAdapter;
import com.cdyhrj.fast.orm.annotation.ColDefine;
import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.IdOneToOne;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程实例对应流程信息
 *
 * @author 黄奇
 */
@Table(name = "wf_process_instance_wf")
@Data
public class ProcessInstanceWf extends BaseEntity {
    /**
     * 流程实例Id
     */
    @Column
    @IdOneToOne
    private Long processInstanceId;

    /**
     * 流程信息
     */
    @Column
    @ColDefine(adapter = ObjectValueAdapter.class)
    private RuntimeWf runtimeWf;
}
