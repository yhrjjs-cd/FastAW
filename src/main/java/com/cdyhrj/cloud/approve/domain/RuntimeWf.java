package com.cdyhrj.cloud.approve.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 运行时流程信息
 *
 * @author 黄奇
 */
@Data
public class RuntimeWf {
    /**
     * 模版Id
     */
    private String templateId;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 步骤信息
     */
    @NotNull(message = "步骤信息不能为空")
    private List<Step> steps;
}
