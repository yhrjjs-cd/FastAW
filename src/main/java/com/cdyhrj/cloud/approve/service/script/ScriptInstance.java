package com.cdyhrj.cloud.approve.service.script;

import com.googlecode.aviator.AviatorEvaluator;

import java.util.Map;

/**
 * 脚本Instance
 *
 * @author huangqi
 */
public class ScriptInstance {
    static {
        AviatorEvaluator.addFunction(new GetPersonFunction());
        AviatorEvaluator.addFunction(new GetSubmitDataFunction());
    }

    public static Object execute(String script, Map<String, Object> submitData) {
        return AviatorEvaluator.execute(script, AviatorEvaluator.newEnv("SUBMIT_DATA", submitData));
    }
}
