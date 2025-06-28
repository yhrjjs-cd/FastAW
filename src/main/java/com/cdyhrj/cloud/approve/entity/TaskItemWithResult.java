package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.Entity;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务单,包含结果
 *
 * @author 黄奇
 */
@Table(name = "v_wf_task_item")
@Data
public class TaskItemWithResult implements Entity {
    /**
     * 任务Title
     */
    @Column
    private String title;

    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;
//
//    /**
//     * 处理人Guid
//     */
//    @Column
//    private String executorId;

    /**
     * 处理人姓名
     */
    @Column
    private String executorName;

//    /**
//     * 开始时间
//     */
//    @Column
//    private Date startTime;
//
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

//    /**
//     * 次序
//     */
//    @Column
//    private int itemIndex;
//
//    /**
//     * 企业Id
//     */
//    @Column
//    private long tenantId;

    /**
     * 审批结果
     */
    @Column
    private String result;
}
