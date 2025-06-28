package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.domain.Step;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * (iTek-china 2023)
 * <p>
 * 步骤实用类
 *
 * @author huangqi
 * <pre>
 *   2023-09-22 * 黄奇创建
 * </pre>
 */
@Component
@RequiredArgsConstructor
public final class StepUtils {
    /**
     * 根据步骤获取流程描述信息（最大100字）
     * 格式：审批节点-第一个人等N人（审批中）
     *
     * @param step 步骤
     * @return 流程描述实例信息
     */
    public static String extractStepTipInfo(Step step) {
        if (Objects.isNull(step.getPersonList()) || step.getPersonList().isEmpty()) {
            return "自动审批";
        }

        String stepName = step.getName();
        String firstPerson = step.getPersonList().get(0).getName();
        int approveCount = step.getPersonList().size();

        String info;
        if (approveCount == 1) {
            info = String.format("%s-%s(审批中)", stepName, firstPerson);
        } else {
            info = String.format("%s-%s等%d人(审批中)", stepName, firstPerson, approveCount);
        }

        return StringUtils.abbreviate(info, 100);
    }

    /**
     * step 添加到 列表， 直接上级步骤根据员工类型，需要转为多个
     *
     * @param step  step
     * @param steps step 列表
     */
    public Long addStepTo(Step step, List<Step> steps, Long userId) {
        return 0L;
//        if (Objects.isNull(userId)) {
//            steps.add(step);
//
//            return null;
//        } else {
//            if (step.getSelectType() != SelectType.DirectSupervisor) {
//                steps.add(step);
//
//                return userId;
//            }
//
//            List<Person> personList = approvalUserService.getSearchValue(userId);
//            if (personList.isEmpty()) {
//                throw new RuntimeException("不能查到上级，不能发起审批");
//            }
//
//            ((DirectSupervisorConfig) step.getApproveConfig()).setPersonList(personList);
//            steps.add(step);
//
//            return personList.get(0).getId();
//        }
    }
}
