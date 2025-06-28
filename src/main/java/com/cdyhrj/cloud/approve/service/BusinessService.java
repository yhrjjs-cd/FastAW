package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.api.IAfterWorkflowCompleteHandler;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.enums.ProcessInstanceStatus;
import com.cdyhrj.cloud.approve.util.SpringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 业务服务， 订单完成处理
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Service
@RequiredArgsConstructor
public class BusinessService {
    /**
     * 流程完成业务处理， 以后需要改为消息方式
     *
     * @param processInstance 流程实例
     */
    public void afterWorkflowComplete(ProcessInstance processInstance) {
        IAfterWorkflowCompleteHandler handler = SpringUtils.getBean(processInstance.getBizType(), IAfterWorkflowCompleteHandler.class);

        handler.complete(processInstance.getBizId(), processInstance.getStatus() == ProcessInstanceStatus.Finished);
    }
}
