package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fastorm.annotation.Column;
import com.cdyhrj.fastorm.annotation.Table;
import com.cdyhrj.fastorm.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程附件信息
 * (iTek-china 2022)
 *
 * @author <a href="huangqi@itek-china.com">黄奇</a>
 * @version 7.0
 * <pre>
 *   2022-10-20 * 黄奇创建
 * </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_process_instance_ps")
public class ProcessInstanceAttachment implements Entity {
    /**
     * 附件Id
     */
    @Column
    private Long id;

    /**
     * 流程Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 附件名称
     */
    @Column
    private String name;
}
