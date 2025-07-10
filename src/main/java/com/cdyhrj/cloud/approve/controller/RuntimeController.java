
package com.cdyhrj.cloud.approve.controller;

import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.ApprovalInfo;
import com.cdyhrj.cloud.approve.domain.ForwardInfo;
import com.cdyhrj.cloud.approve.domain.StartProcessInfo;
import com.cdyhrj.cloud.approve.domain.StepInfo;
import com.cdyhrj.cloud.approve.domain.TaskApproveObject;
import com.cdyhrj.cloud.approve.entity.CommentItem;
import com.cdyhrj.cloud.approve.entity.ProcessInstance;
import com.cdyhrj.cloud.approve.enums.TaskStatus;
import com.cdyhrj.cloud.approve.service.ProcessInstanceService;
import com.cdyhrj.cloud.approve.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;


/**
 * 流程实例
 *
 * @author 黄奇
 */
@RestController
@RequestMapping("/wf/runtime")
@RequiredArgsConstructor
public class RuntimeController {
    private final ProcessInstanceService processInstanceService;
    private final TaskService taskService;
    private final IAwUserContext awUserContext;

    /**
     * 发起流程
     *
     * @param startProcessInfo 启动信息
     */
    @PostMapping(value = "/start-process")
    public Map<String, Long> startProcess(@Valid @RequestBody StartProcessInfo startProcessInfo) {
        ProcessInstance processInstance = processInstanceService.start(startProcessInfo);

        return Collections.singletonMap("id", processInstance.getId());
    }

    /**
     * 完成工单-同意
     *
     * @param taskApproveObject 提交对象
     */
    @PostMapping(value = "/approve-task")
    public Map<String, Long> completeTask(@Valid @RequestBody TaskApproveObject taskApproveObject) {
        return Collections.singletonMap("id", taskService.execTaskItem(taskApproveObject, TaskStatus.Approved));
    }

    /**
     * 拒绝驳回
     *
     * @param taskApproveObject 提交对象
     */
    @PostMapping(value = "/reject-task")
    public Map<String, Long> withdrawTask(@Valid @RequestBody TaskApproveObject taskApproveObject) {
        return Collections.singletonMap("id", taskService.execTaskItem(taskApproveObject, TaskStatus.Rejected));
    }

    /**
     * 撤销流程
     *
     * @param processInstanceId 流程实例Guid
     */
    @PostMapping(value = "/cancel-process")
    public Map<String, Long> cancelPrecess(@RequestParam Long processInstanceId) {
        return Collections.singletonMap("id", processInstanceService.cancelProcess(processInstanceId));
    }

    /**
     * 添加转发人
     *
     * @param forwardInfo 转发信息
     */
    @PostMapping(value = "/forward")
    public void forward(@Valid @RequestBody ForwardInfo forwardInfo) {
        processInstanceService.forward(forwardInfo);
    }

    /**
     * 评论
     *
     * @param commentItem 评论内容
     * @return 评论对象Guid
     */
    @PostMapping(value = "/comment")
    public Map<String, Long> comment(@Valid @RequestBody CommentItem commentItem) {
        return Collections.singletonMap("id", processInstanceService.comment(commentItem));
    }

    /**
     * 获取步骤
     *
     * @param id        流程Id
     * @param dataValue 提交数据
     * @return 步骤
     */
    @PostMapping(value = "/step-info")
    public StepInfo getStepInfo(@RequestParam String id, @RequestBody Map<String, Object> dataValue) {
        return processInstanceService.getStepInfo(id, awUserContext, dataValue);
    }

    /**
     * 获取审批项
     *
     * @param processInstanceId 流程实例Guid
     * @return 审批清单
     */
    @PostMapping(value = "/approval-info")
    public ApprovalInfo getApprovalInfo(@RequestParam Long processInstanceId) {
        return processInstanceService.getApprovalInfo(processInstanceId);
    }
}
