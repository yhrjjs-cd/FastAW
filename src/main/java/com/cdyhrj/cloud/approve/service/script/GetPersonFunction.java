package com.cdyhrj.cloud.approve.service.script;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;
import java.util.Objects;

/**
 * 获取员工信息
 *
 * @author 黄奇
 */
public class GetPersonFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "GetPerson";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg) {
        Number personId = FunctionUtils.getNumberValue(arg, env);
        Objects.requireNonNull(personId, "审批用户Id为空");

        //
//        SqlClient sqlClient = SpringUtils.getBean(SqlClient.class);
//
//        User user = sqlClient.objectQuery()
//                .id(personId.intValue())
//                .fetch(User.class);
//
//        Objects.requireNonNull(user, "审批用户为空");
//        return FunctionUtils.wrapReturn(IdName.of(user.getId(), user.getName()));
        return null;
    }
}
