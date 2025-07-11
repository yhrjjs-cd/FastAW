package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.cdyhrj.cloud.approve.api.IAwSelectPersonService;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
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
public class ScriptExpressionArg implements SelectTypeArg {
    private SelectType type = SelectType.ScriptExpression;

    /**
     * 脚本字符串
     */
    private String script;

    @Override
    public List<IdName> calcPersonList(IAwUserContext userContext, Map<String, Object> submitData) {
        return AwSpringUtils.getBean(IAwSelectPersonService.class).selectByScript(script, submitData);
    }
}
