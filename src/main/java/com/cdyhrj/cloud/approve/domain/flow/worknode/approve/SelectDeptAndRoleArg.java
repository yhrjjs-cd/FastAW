package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.IAwSelectPersonService;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;
import com.cdyhrj.cloud.approve.util.AwSpringUtils;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class SelectDeptAndRoleArg implements SelectTypeArg {
    private SelectType type = SelectType.SelectDeptAndRole;


    /**
     * 部门列表
     */
    private List<IdName> deptList;

    /**
     * 角色列表
     */
    private List<IdName> roleList;

    @Override
    public List<IdName> calcPersonList(IAwUserContext userContext, Map<String, Object> submitData) {
        Assert.isTrue(Objects.nonNull(deptList) && !deptList.isEmpty(), "必须配置部门");
        Assert.isTrue(Objects.nonNull(roleList) && !roleList.isEmpty(), "必须配置角色");

        List<Long> deptIds = deptList.stream().map(IdName::getId).toList();
        List<Long> roleIds = roleList.stream().map(IdName::getId).toList();

        return AwSpringUtils.getBean(IAwSelectPersonService.class).selectDeptAndRoleIds(deptIds, roleIds);
    }
}
