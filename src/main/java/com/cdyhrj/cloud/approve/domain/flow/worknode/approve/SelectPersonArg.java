package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
public class SelectPersonArg implements SelectTypeArg {
    private SelectType type = SelectType.SelectPerson;

    /**
     * 被选择的人员
     */
    private IdName[] personList;

    @Override
    public List<IdName> calcPersonList(IAwUserContext userContext, Map<String, Object> submitData) {
        return Arrays.stream(personList).toList();
    }
}
