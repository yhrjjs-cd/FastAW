package com.cdyhrj.cloud.approve.domain.flow.worknode.branch;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class Condition implements Serializable {
    /**
     * type: "PROMOTER" | "DATA"
     */
    private String type;

    /**
     * 中文名称
     */
    private String caption;

    /**
     * 参数
     */
    private String param;

    /**
     * 操作符
     */
    private String operator;

    /**
     * 值
     */
    private Object value;

    /**
     * 名称
     */
    private String name;
}
