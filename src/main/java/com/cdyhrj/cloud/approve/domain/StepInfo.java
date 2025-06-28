package com.cdyhrj.cloud.approve.domain;

import lombok.Data;

import java.util.List;

/**
 * 步骤对象
 * (iTek-china 2022)
 *
 * @author <a href="huangqi@itek-china.com">黄奇</a>
 * @version 7.0
 * <pre>
 *   2022-11-02 * 黄奇创建
 * </pre>
 */
@Data
public class StepInfo {
    /**
     * 流程模版Id
     */
    private String templateId;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 步骤对象
     */
    private List<Step> steps;
}
