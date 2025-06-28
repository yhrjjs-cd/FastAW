package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 流程任务单
 *
 * @author 黄奇
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_task_item")
public class TaskItem extends BaseEntity {
    /**
     * 审批意见，最大长度 40，如果超过40，写入wf_task_item_clob表中
     */
    public static final int MAX_OPINION_FIELD_LENGTH = 40;

    /**
     * 任务Id
     */
    @Column
    private Long taskId;

    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 处理人Guid
     */
    @Column
    private Long executorId;

    /**
     * 任务标题
     */
    @Column
    private String title;

    /**
     * 处理人姓名
     */
    @Column
    private String executorName;

    /**
     * 开始时间
     */
    @Column
    private Date startTime;

    /**
     * 结束时间
     */
    @Column
    private Date endTime;

    /**
     * 流程状态
     */
    @Column
    private TaskStatus status;

    /**
     * 次序
     */
    @Column
    private int itemIndex;

    /**
     * 审批意见
     */
    @Column
    private String opinion;
}
