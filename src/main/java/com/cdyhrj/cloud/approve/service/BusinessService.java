package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.api.IAwCompleteHandler;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.cloud.approve.util.AwSpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 业务服务， 订单完成处理
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Slf4j
@Service
public class BusinessService {
    /**
     * 流程完成业务处理， 以后需要改为消息方式
     *
     * @param processInstance 流程实例
     */
    public void afterWorkflowComplete(ProcessInstance processInstance) {
        if (Objects.isNull(processInstance.getBizType())) {
            if (log.isWarnEnabled()) {
                log.warn("processInstance bizType is null");
            }

            return;
        }

        IAwCompleteHandler handler = AwSpringUtils.getBean(processInstance.getBizType(), IAwCompleteHandler.class);

        handler.complete(processInstance.getBizId(), processInstance.getStatus() == ProcessInstanceStatus.Finished);
    }
}
