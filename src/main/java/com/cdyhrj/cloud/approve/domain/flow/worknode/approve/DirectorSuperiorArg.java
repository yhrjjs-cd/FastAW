package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.IAwSelectPersonService;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;
import com.cdyhrj.cloud.approve.util.AwSpringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Data
@Slf4j
public class DirectorSuperiorArg implements SelectTypeArg {
    private SelectType type = SelectType.DirectorSuperior;

    @Override
    public List<IdName> calcPersonList(Map<String, Object> submitData) {
        return AwSpringUtils.getBean(IAwSelectPersonService.class).selectDirectorSuperior();
    }
}
