package com.cdyhrj.cloud.approve.domain.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * 参数信息
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class Param implements Serializable {
    /**
     * 参数名称
     */
    private String name;

    /**
     * 中文名称
     */
    private String caption;

    /**
     * 参数类型
     */
    private ParamType type;

    /**
     * 数据字典ID
     */
    private String dataDictId;

    /**
     * 参数类型
     */
    public enum ParamType {
        /**
         * 字符串
         */
        STRING,

        /**
         * 数字
         */
        NUMBER,

        /**
         * 布尔
         */
        BOOLEAN
    }
}
