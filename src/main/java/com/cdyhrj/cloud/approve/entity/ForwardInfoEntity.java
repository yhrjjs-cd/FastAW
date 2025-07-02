package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 转发信息
 *
 * @author 黄奇
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_forward_info")
public class ForwardInfoEntity extends BaseEntity {
    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 转发人
     */
    @Column
    private Long fromEmpId;

    /**
     * 转发人名称
     */
    @Column
    private String fromEmpName;

    /**
     * 职务信息
     */
    @Column
    private String duty;

    /**
     * 转发备注
     */
    @Column
    private String comments;

    /**
     * 转发信息
     */
    @Column
    private String info;

    /**
     * 是否审批
     */
    @Column
    private Boolean isApproval;
}
