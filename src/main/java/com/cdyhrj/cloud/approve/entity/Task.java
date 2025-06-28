package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SignRule;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 流程任务
 *
 * @author 黄奇
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_task")
public class Task extends BaseEntity {
    /**
     * 任务类型
     */
    @Column
    private NodeType nodeType;

    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 处理人，
     */
    @Column
    private String currTaskExecutors;

    /**
     * 任务标题
     */
    @Column
    private String title;
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
     * 开始时间
     */
    @Column
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column
    private LocalDateTime endTime;

    /**
     * 任务状态
     */
    @Column
    private TaskStatus status;

    /**
     * 待签数量
     */
    @Column
    private int toSignNum;

    /**
     * 已签数量
     */
    @Column
    private int signedNum;

    /**
     * 会签规则
     */
    @Column
    private SignRule signRule;

    /**
     * 任务次序
     */
    @Column
    private int taskIndex;

    /**
     * 原始任务次序，用于获取下一步骤
     */
    @Column
    private int originTaskIndex;
}
