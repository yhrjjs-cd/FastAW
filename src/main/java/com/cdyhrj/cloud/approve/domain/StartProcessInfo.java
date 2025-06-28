package com.cdyhrj.cloud.approve.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 流程启动参数信息
 *
 * @author 黄奇
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartProcessInfo {
    /**
     * 业务类型，以Guid定义，业务唯一
     */
    private String bizType;

    /**
     * 业务Id
     */
    private Long bizId;

    /**
     * 发起人
     */
    @NotNull(message = "发起人不能为空")
    private Long promoter;

    /**
     * 发起人姓名
     */
    @NotNull(message = "发起人姓名不能为空")
    private String promoterName;

    /**
     * 附件信息
     */
    private List<IdName> attachments;

    /**
     * 运行时流程信息
     */
    @NotNull(message = "运行时流程信息不能为空")
    private RuntimeWf runtimeWf;

    /**
     * 业务数据
     */
    private Object bizData;

    public static StartProcessInfo of(String bizType, Long bizId, Long promoter, String promoterName, RuntimeWf runtimeWf) {
        StartProcessInfo startProcessInfo = new StartProcessInfo();

        startProcessInfo.setBizType(bizType);
        startProcessInfo.setBizId(bizId);
        startProcessInfo.setPromoter(promoter);
        startProcessInfo.setPromoterName(promoterName);
        startProcessInfo.setRuntimeWf(runtimeWf);

        return startProcessInfo;
    }
}
