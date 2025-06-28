package com.cdyhrj.cloud.approve.service.script;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * GetPerson( GetSubmitData("shiftsUserId"))
 * 通过Key获取提交值
 *
 * @author 黄奇
 */
public class GetSubmitDataFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "GetSubmitData";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
        @SuppressWarnings("unchecked")
        Map<String, Object> submitData = (Map<String, Object>) env.get("SUBMIT_DATA");

        String key = FunctionUtils.getStringValue(arg, env);

        return FunctionUtils.wrapReturn(submitData.get(key));
    }
}
