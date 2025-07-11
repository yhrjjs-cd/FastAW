package com.cdyhrj.cloud.approve.domain.flow.worknode.cc;

import com.cdyhrj.cloud.approve.api.IAwSelectPersonService;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;
import com.cdyhrj.cloud.approve.domain.flow.worknode.Arg;
import com.cdyhrj.cloud.approve.domain.flow.worknode.WorkNode;
import com.cdyhrj.cloud.approve.util.AwSpringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class CcArg implements Arg {
    private NodeType nodeType = NodeType.CC;
    private String title;
    /**
     * 是否抄送给自己
     */
    private boolean ccToSelf;

    /**
     * 是否抄送给直接主管
     */
    private boolean ccDirectorSuperior;

    /**
     * 根据角色
     */
    private List<IdName> roleArr;

    /**
     * 根据部门角色选择
     */
    private DeptAndRole deptAndRole;

    @Override
    public void writeStepsTo(IAwUserContext userContext, List<Step> steps, Map<String, Object> dataValue, WorkNode contextNode) {
        Step step = Step.builder()
                .id(contextNode.getId())
                .nodeType(this.nodeType)
                .name(this.title)
                .personList(this.calcPersonList(userContext))
                .build();

        steps.add(step);

        WorkNode next = contextNode.getNext();
        if (Objects.nonNull(next)) {
            next.writeStepsTo(userContext, steps, dataValue);
        }
    }

    private List<IdName> calcPersonList(IAwUserContext userContext) {
        List<IdName> personList = new ArrayList<>();

        IAwSelectPersonService selectPersonService = AwSpringUtils.getBean(IAwSelectPersonService.class);
        if (this.ccToSelf) {
            personList.add(IdName.of(userContext.getUserId(), userContext.getUserName()));
        }

        if (this.ccDirectorSuperior) {
            personList.addAll(selectPersonService.selectDirectorSuperior(userContext.getUserId()));
        }

        if (Objects.nonNull(this.roleArr) && !this.roleArr.isEmpty()) {
            List<Long> roleIds = roleArr.stream().map(IdName::getId).toList();
            personList.addAll(selectPersonService.selectByRoleIds(roleIds));
        }

        if (Objects.nonNull(deptAndRole)) {
            List<IdName> deptIdNames = deptAndRole.deptArr;
            List<IdName> roleIdNames = deptAndRole.roleArr;
            if ((Objects.nonNull(deptIdNames) && !deptIdNames.isEmpty()) &&
                    (Objects.nonNull(roleIdNames) && !roleIdNames.isEmpty())) {
                List<Long> deptIds = deptIdNames.stream().map(IdName::getId).toList();
                List<Long> roleIds = roleIdNames.stream().map(IdName::getId).toList();

                personList.addAll(selectPersonService.selectDeptAndRoleIds(deptIds, roleIds));
            }
        }

        return personList;
    }

    @Data
    public static class DeptAndRole implements Serializable {
        private List<IdName> deptArr;
        private List<IdName> roleArr;
    }
}
