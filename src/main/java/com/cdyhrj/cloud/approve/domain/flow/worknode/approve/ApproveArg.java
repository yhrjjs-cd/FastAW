package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectPersonType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;
import com.cdyhrj.cloud.approve.domain.flow.enums.SignRule;
import com.cdyhrj.cloud.approve.domain.flow.worknode.Arg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 审批节点参数
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
@Slf4j
public class ApproveArg implements Arg {
    private NodeType nodeType = NodeType.APPROVE;
    private String title;
    private SelectType selectType;
    private SelectTypeArg selectTypeArg;
    private SelectPersonType selectPersonType;
    private SignRule signRule;

    @Override
    public void writeStepsTo(IAwUserContext awUserContext, List<Step> steps, Map<String, Object> dataValue, WorkNode contextNode) {
        Step step = Step.builder()
                .id(contextNode.getId())
                .nodeType(this.nodeType)
                .name(this.title)
                .signRule(this.signRule)
                .personList(this.calcPersonList(awUserContext, dataValue))
                .build();

        steps.add(step);

        WorkNode next = contextNode.getNext();
        if (Objects.nonNull(next)) {
            next.writeStepsTo(awUserContext, steps, dataValue);
        }
    }

    private List<IdName> calcPersonList(IAwUserContext userContext, Map<String, Object> dataValue) {
        return selectTypeArg.calcPersonList(userContext, dataValue);
    }
}
