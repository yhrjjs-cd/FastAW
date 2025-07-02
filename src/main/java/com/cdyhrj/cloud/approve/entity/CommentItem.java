package com.cdyhrj.cloud.approve.entity;

import com.cdyhrj.fast.orm.annotation.Column;
import com.cdyhrj.fast.orm.annotation.Table;
import com.cdyhrj.fast.orm.entity.BaseEntity;
import lombok.Data;

/**
 * 转发内容
 *
 * @author 黄奇
 */
@Data
@Table(name = "wf_comment_item")
public class CommentItem extends BaseEntity {
    /**
     * 流程实例Id
     */
    @Column
    private Long processInstanceId;

    /**
     * 评论人
     */
    @Column
    private Long commentId;

    /**
     * 评论人名称
     */
    @Column
    private String commentName;

    /**
     * 评论内容
     */
    @Column
    private String content;
}
