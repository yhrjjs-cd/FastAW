package com.cdyhrj.cloud.approve.domain.flow.worknode.approve;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.cdyhrj.cloud.approve.api.IAwUserContext;
import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.domain.flow.enums.SelectType;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 选择类型参数
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@JSONType(deserializer = SelectTypeArg.ArgDeserializer.class)
public interface SelectTypeArg extends Serializable {
    /**
     * 选择类型
     *
     * @return SelectType
     */
    SelectType getType();

    /**
     * 计算审批人员信息
     *
     * @param userContext 发起人上下文
     * @param submitData  提交的数据
     * @return 审批人员信息
     */
    List<IdName> calcPersonList(IAwUserContext userContext, Map<String, Object> submitData);

    class ArgDeserializer implements ObjectReader<SelectTypeArg> {
        static final String MATCH_FIELD = "type";

        @Override
        public SelectTypeArg readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            String jsonStr = jsonReader.readString();

            JSONObject json = JSON.parseObject(jsonStr);

            SelectType selectType = SelectType.valueOf(json.getString(MATCH_FIELD));

            return json.toJavaObject(selectType.getTypeArgClass());
        }
    }
}
