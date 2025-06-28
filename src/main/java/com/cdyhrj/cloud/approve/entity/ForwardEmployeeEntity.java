package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 转发人员
 *
 * @author 黄奇
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_forward_employee")
public class ForwardEmployeeEntity extends BaseEntity {
    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 可评论人
     */
    @Column
    private Long empId;

    /**
     * 转发人
     */
    @Column
    private Long fromEmpId;
}
