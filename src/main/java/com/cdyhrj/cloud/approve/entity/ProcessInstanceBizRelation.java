package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Id;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程业务关系
 * (iTek-china 2022)
 *
 * @author huangqi
 * <pre>
 *   2023-09-22 * 黄奇创建
 * </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_process_instance_biz_rel")
public class ProcessInstanceBizRelation implements Entity {
    /**
     * Inner Id，Long type
     */
    @Id
    @Column
    private Long id;

    /**
     * 企业Id
     */
    @Column
    private Long tenantId;

    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 流程标题
     */
    @Column
    private String title;

    /**
     * 业务类型，GUID
     */
    @Column
    private String bizType;
    /**
     * 业务guid
     */
    @Column
    private Long bizId;

    /**
     * 流程状态
     */
    @Column
    private ProcessInstanceStatus processStatus;

    /**
     * 流程信息
     */
    @Column
    private String processInfo;
}
