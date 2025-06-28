package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.domain.flow.Flow;
import com.cdyhrj.fastorm.adapter.ObjectValueAdapter;
import com.cdyhrj.fastorm.annotation.ColDefine;
import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程模版
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "wf_template")
public class WfTemplate extends BaseEntity {
    // TODO here innerEntity
    /**
     * 编码
     */
    @Column
    private String wfType;

    /**
     * 模板名称
     */
    @Column
    private String description;

    /**
     * 应用系统
     */
    @Column
    @ColDefine(adapter = ObjectValueAdapter.class)
    private Flow template;
}
