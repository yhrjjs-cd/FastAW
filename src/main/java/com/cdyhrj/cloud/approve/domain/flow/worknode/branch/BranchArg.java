package com.cdyhrj.cloud.approve.domain.flow.worknode.branch;

import com.alibaba.fastjson2.JSON;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.worknode.Arg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import com.cdyhrj.cloud.approve.util.SpringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
@Slf4j
public class BranchArg implements Arg {
    private NodeType nodeType = NodeType.BRANCH;
    /**
     * 是否显示已关闭,Only for front
     */
    private Boolean showClosed;

    /**
     * Branch节点
     */
    private BranchItem[] children;

    /**
     * 支持默认分支
     */
    private Boolean supportDefault;


    @Override
    public void writeStepsTo(List<Step> steps, Map<String, Object> dataValue, WorkNode contextNode) {
        int len = children.length;

        if (BooleanUtils.isTrue(this.supportDefault)) {
            writeStepsWithDefault(steps, dataValue, len);
        } else {
            writeStepsWithOutDefault(steps, dataValue, len, contextNode);
        }

        WorkNode next = contextNode.getNext();
        if (Objects.nonNull(next)) {
            next.writeStepsTo(steps, dataValue);
        }
    }

    private void writeStepsWithDefault(List<Step> steps, Map<String, Object> dataValue, int len) {
        for (int i = 0; i < len; i++) {
            BranchItem item = children[i];

            if (i == len - 1) {
                // 默认项不比较，直接通过
                if (Objects.nonNull(item.getNode())) {
                    item.getNode().writeStepsTo(steps, dataValue);
                }

                break;
            }

            if (testIsTrue(item.getConditions(), item.getLinkMethod(), dataValue)) {
                if (Objects.nonNull(item.getNode())) {
                    item.getNode().writeStepsTo(steps, dataValue);
                }

                break;
            }
        }
    }

    private void writeStepsWithOutDefault(List<Step> steps, Map<String, Object> dataValue, int len, WorkNode contextNode) {
        boolean passed = false;

        for (int i = 0; i < len; i++) {
            BranchItem item = children[i];

            if (testIsTrue(item.getConditions(), item.getLinkMethod(), dataValue)) {
                passed = true;
                if (Objects.nonNull(item.getNode())) {
                    item.getNode().writeStepsTo(steps, dataValue);
                }

                break;
            }
        }

        if (!passed) {
            throw new RuntimeException("不满足审批条件，不能获取审批步骤; Id:" + contextNode.getId());
        }
    }

    /**
     * 检查是否满足条件
     *
     * @param conditions 条件
     * @param linkMethod 连接方法
     * @param dataValue  数据集
     * @return 是否满足
     */
    private boolean testIsTrue(Condition[] conditions, LinkMethod linkMethod, Map<String, Object> dataValue) {
        if (linkMethod == LinkMethod.AND) {
            for (Condition c : conditions) {
                if (!testCondition(c, dataValue)) {
                    return false;
                }
            }

            return true;
        } else {
            for (Condition c : conditions) {
                if (testCondition(c, dataValue)) {
                    return true;
                }
            }

            return false;
        }
    }

    private boolean testCondition(Condition c, Map<String, Object> dataValue) {
        if ("PROMOTER".equals(c.getType())) {
            // 查询发起人
            return testPromoterValue(c.getParam(), c.getValue());
        } else {
            log.info(JSON.toJSONString(c));
        }

        return testDataValue(c.getParam(), c.getOperator(), c.getValue(), dataValue);
    }

    /**
     * 测试发起人值,当前只支持角色
     *
     * @param param 参数名
     * @param value 值
     * @return 是否匹配
     */
    private boolean testPromoterValue(String param, Object value) {
        if ("角色".equals(param)) {
            IAwUserContext userContext = SpringUtils.getBean(IAwUserContext.class);
            return Objects.nonNull(value) && value.equals(userContext.getRoleId());
        }

        return false;
    }

    /**
     * 测试数据
     */
    private boolean testDataValue(String param, String operator, Object value, Map<String, Object> dataValue) {
        double dv = Double.parseDouble(String.valueOf(dataValue.get(param)));

        return switch (operator) {
            case "=" -> testEqual(value, dataValue.get(param));
            case ">" -> dv > Double.parseDouble(String.valueOf(value));
            case ">=" -> dv >= Double.parseDouble(String.valueOf(value));
            case "<" -> dv < Double.parseDouble(String.valueOf(value));
            case "<=" -> dv <= Double.parseDouble(String.valueOf(value));
            default -> false;
        };
    }

    private boolean testEqual(Object value, Object dv) {
        if ("$USER_ID".equals(value)) {
            IAwUserContext userContext = SpringUtils.getBean(IAwUserContext.class);
            return userContext.getUserId().intValue() == (int) dv;
        }

        return value.equals(dv);
    }
}
