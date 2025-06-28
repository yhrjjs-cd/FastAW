package com.cdyhrj.cloud.approve.service;

import com.alibaba.fastjson2.JSON;
import com.cdyhrj.cloud.approve.domain.flow.Flow;
import com.cdyhrj.cloud.approve.entity.WfTemplate;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 模板服务
 * (iTek-china 2022)
 *
 * @author <a href="huangqi@itek-china.com">黄奇</a>
 * @version 7.0
 * <pre>
 *   2022-10-14 * 黄奇创建
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class TemplateService {
    private final FastORM fastORM;

    /**
     * 保存配置
     *
     * @param id   id
     * @param flow 配置信息
     */
    public void saveConfig(String id, Flow flow) {
        fastORM.updatable(WfTemplate.class)
                .name(id)
                .updateField(WfTemplate::getTemplate, JSON.toJSONString(flow));
    }

    /**
     * 获取配置
     *
     * @param id id
     * @return 配置信息
     */
    public Flow getConfig(String id) {
        return fastORM.fetchable(WfTemplate.class)
                .name(id)
                .fetch()
                .orElseThrow()
                .getTemplate();
    }
}
