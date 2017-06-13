package intrepreter.manager.impl;

import com.alibaba.fastjson.JSON;
import intrepreter.Validator.CheckJson;
import intrepreter.Validator.impl.CheckJsonImpl;
import intrepreter.constains.JsonTypeEnum;
import intrepreter.domain.JsonObejct;
import intrepreter.manager.JsonInterpreter;
import intrepreter.manager.JsonRuleFilter;
import test.TestBean;

import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public class JsonInterpreterImpl implements JsonInterpreter {

    JsonRuleFilter jsonRuleFilter = new JsonRuleFilterImpl();

    @Override
    public Map<String, JsonObejct> interpreterJsonString(String jsonString) {
        JsonTypeEnum jsonTypeEnum = jsonRuleFilter.filterRule(jsonString);
        switch (jsonTypeEnum) {
            case NULL:
                break;
            case NUMBER:
            case STRING:
            case BOOLEAN:
            case ARRAY:
            case OBJECT:
            default:
        }
        return null;
    }

    @Override
    public Map<String, String> scanJsonString(String jsonString) {
        /**
         * 校验参数
         */
        CheckJson checkJson = new CheckJsonImpl();
        checkJson.checkParam(jsonString);
        String json=jsonString.trim().substring(1,jsonString.trim().length()-1);
        System.out.println(json);
        String[] str = json.split(":");
        for (String string : str) {
            System.out.println(string);
        }

        return null;
    }

    @Override
    public Object praseNum(String jsonString) {
        return Double.parseDouble(jsonString);
    }

    @Override
    public String praseString(String jsonString) {
        return jsonString;
    }

    @Override
    public Boolean praseBoolean(String jsonString) {
        return Boolean.valueOf(jsonString);
    }

    @Override
    public List<Object> praseArray(String jsonString) {
        return null;
    }

    @Override
    public Map<String, JsonObejct> praseObject(String jsonString) {
        return null;
    }

    @Override
    public Object praseNull(String jsonString) {
        return null;
    }

    public static void main(String[] args) {
        TestBean result = new TestBean();
        String jsonString = JSON.toJSONString(result);

        System.out.println(jsonString);
        JsonInterpreter jsonInterpreter = new JsonInterpreterImpl();
        jsonInterpreter.scanJsonString(jsonString);
    }
}
