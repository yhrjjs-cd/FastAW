package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.api.IUserContext;
import com.cdyhrj.cloud.approve.domain.StartProcessInfo;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.entity.Task;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流程启动服务
 *
 * @author 黄奇
 */
@Service
@RequiredArgsConstructor
public class StartService {
    private final FastORM fastORM;
    private final IUserContext userContext;
    private final ExecuteNextTaskService executeNextTaskService;

    /**
     * 获取步骤待审批人数
     *
     * @param step 步骤
     * @return 审批人数
     */
    private int getToSignNum(Step step) {
        return step.getPersonList().size();
    }


    /**
     * 启动任务，使用Start
     *
     * @param startProcessInfo 启动信息
     * @param processInstance  流程实例信息
     */
    public void start(StartProcessInfo startProcessInfo, ProcessInstance processInstance) {
        Objects.requireNonNull(startProcessInfo.getRuntimeWf(), "审批信息不能为空");
        Objects.requireNonNull(startProcessInfo.getRuntimeWf().getSteps(), "审批步骤不能为空");

        // 插入所有的任务
        long tenantId = userContext.getTenantId();
        AtomicInteger index = new AtomicInteger(1);
        List<Task> tasks = startProcessInfo.getRuntimeWf()
                .getSteps()
                .stream()
                .map(step -> {
                    Task task = Task.builder()
                            .processInstanceId(processInstance.getId())
                            .promoterId(processInstance.getPromoterId())
                            .promoterName(processInstance.getPromoterName())
                            .nodeType(step.getNodeType())
                            .title(step.getName())
                            .currTaskExecutors(step.executorNames())
                            .status(TaskStatus.Created)
                            .toSignNum(getToSignNum(step))
                            .signedNum(0)
                            .signRule(step.getSignRule())
                            .originTaskIndex(index.get())
                            .taskIndex(index.getAndIncrement())
                            .build();
                    task.setTenantId(tenantId);
                    return task;
                }).toList();

        fastORM.insertable(tasks).insert();

        // 执行下一个任务
        executeNextTaskService.execNextTask(processInstance);
    }
}