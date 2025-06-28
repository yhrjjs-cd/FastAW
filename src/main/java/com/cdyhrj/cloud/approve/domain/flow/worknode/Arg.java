package com.cdyhrj.cloud.approve.domain.flow.worknode;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.cdyhrj.cloud.approve.domain.Step;
import com.cdyhrj.cloud.approve.domain.flow.enums.NodeType;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 节点参数对象
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@JSONType(deserializer = Arg.ArgDeserializer.class)
public interface Arg extends Serializable {
    NodeType getNodeType();

    /**
     * 写入步骤信息到步骤列表中
     *
     * @param steps       步骤列表
     * @param dataValue   提交的数据集
     * @param contextNode 所在节点
     */
    void writeStepsTo(List<Step> steps, Map<String, Object> dataValue, WorkNode contextNode);

    class ArgDeserializer implements ObjectReader<Arg> {
        static final String MATCH_FIELD = "nodeType";

        @Override
        public Arg readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            String jsonStr = jsonReader.readString();

            JSONObject json = JSON.parseObject(jsonStr);

            NodeType checkType = NodeType.valueOf(json.getString(MATCH_FIELD));

            return JSON.parseObject(jsonStr, checkType.getNodeClass());
        }
    }
}
