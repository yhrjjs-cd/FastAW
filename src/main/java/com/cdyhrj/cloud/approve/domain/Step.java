package com.cdyhrj.cloud.approve.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SignRule;
import com.cdyhrj.cloud.approve.enums.EmptyApprovalRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 流程实例步骤信息
 *
 * @author 黄奇
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Step {
    /**
     * 主键Id
     */
    private String id;

    /**
     * 节点类型
     */
    private NodeType nodeType;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 会签规则
     */
    private SignRule signRule;

    /**
     * 相关人员信息
     */
    private List<IdName> personList;

    /**
     * 无人审批规则
     */
    @JSONField(serialize = false)
    private EmptyApprovalRule emptyApprovalRule;

    public List<IdName> getPersonList() {
        if (Objects.isNull(personList)) {
            return Collections.emptyList();
        } else {
            return personList;
        }
    }

    /**
     * 获取处理人名，最大显示80位
     */
    public String executorNames() {
        List<String> names = new ArrayList<>();
        if (Objects.nonNull(personList)) {
            for (IdName person : personList) {
                String personName = person.getName();
                names.add(personName);
            }
        }

        return StringUtils.abbreviate(StringUtils.join(names, ","), 80);
    }
}
