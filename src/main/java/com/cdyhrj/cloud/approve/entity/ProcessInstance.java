package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.OneToOne;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 流程实例
 *
 * @author 黄奇
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_process_instance")
public class ProcessInstance extends BaseEntity {
    /**
     * 业务类型，用户Guid定义
     */
    @Column
    private String bizType;


    /**
     * 业务Id键
     */
    @Column
    private Long bizId;

    /**
     * 流程标题
     */
    @Column
    private String title;

    /**
     * 任务描述
     */
    @Column
    private String description;

    /**
     * 发起人
     */
    @Column
    private Long promoterId;

    /**
     * 发起人名称
     */
    @Column
    private String promoterName;

    /**
     * 发起时间
     */
    @Column
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column
    private LocalDateTime endTime;

    /**
     * 当前处理任务
     */
    @Column
    private String currTask;

    /**
     * 当前任务处理人
     */
    @Column
    private String currTaskExecutors;

    /**
     * 流程状态
     */
    @Column
    @Builder.Default
    private ProcessInstanceStatus status = ProcessInstanceStatus.Running;


    /**
     * 流程配置
     */
    @OneToOne(target = ProcessInstanceWf.class)
    @Column
    public ProcessInstanceWf processInstanceWf;
}
