package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wf_cc_item")
public class CcItem implements Entity {
    @Column
    private Long id;

    @Column
    private Long processInstanceId;

    @Column
    private Long taskId;

    @Column
    private String title;

    @Column
    private String subTitle;

    @Column
    private Long toUserId;

    @Column
    private String toUserName;

    @Column
    private TaskStatus status;

    @Column
    private LocalDateTime createAt;

    @Column
    private Long tenantId;
}
