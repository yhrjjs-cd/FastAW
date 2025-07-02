package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.domain.flow.Flow;
import com.cdyhrj.fast.orm.adapter.ObjectValueAdapter;
import com.cdyhrj.fast.orm.annotation.ColDefine;
import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Id;
import com.cdyhrj.fast.orm.annotation.Name;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.Entity;
import lombok.Data;

/**
 * 流程模版
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
@Table(name = "wf_template")
public class WfTemplate implements Entity {
    @Id
    @Column
    private Long innerId;

    @Name
    @Column
    private String id;

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
