package com.cdyhrj.cloud.approve.domain;

import lombok.Data;

import java.util.List;

/**
 * 转发用户信息
 * (iTek-china 2022)
 *
 * @author <a href="huangqi@itek-china.com">黄奇</a>
 * @version 7.0
 * <pre>
 *   2022-10-19 * 黄奇创建
 * </pre>
 */
@Data
public class ForwardInfo {
    /**
     * 流程Guid
     */
    private Long processInstanceId;

    /**
     * 转发信息
     */
    private String info;

    /**
     * 是否审批
     */
    private boolean isApproval;

    /**
     * 转发人
     */
    private List<IdName> personList;
}
