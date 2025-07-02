package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Id;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程任务单Clob数据
 *
 * @author 黄奇
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_task_item_clob")
public class TaskItemClob implements Entity {
    @Id
    @Column
    private Long id;

    /**
     * 任务的Guid
     */
    @Column
    private Long taskItemId;

    /**
     * 审批内容
     */
    @Column
    private String result;
}
