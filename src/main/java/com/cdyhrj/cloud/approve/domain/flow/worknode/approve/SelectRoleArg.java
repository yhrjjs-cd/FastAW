package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.ISelectPersonService;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;
import com.cdyhrj.cloud.approve.util.SpringUtils;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class SelectRoleArg implements SelectTypeArg {
    private SelectType type = SelectType.SelectRole;
    /**
     * 角色列表
     */
    private List<IdName> roleList;

    @Override
    public List<IdName> calcPersonList(Map<String, Object> submitData) {
        if (Objects.isNull(roleList)) {
            return Collections.emptyList();
        }

        List<Long> roleIds = roleList.stream().map(IdName::getId).toList();

        return SpringUtils.getBean(ISelectPersonService.class).selectByRoleIds(roleIds);
    }
}
