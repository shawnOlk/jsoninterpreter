package intrepreter.manager.impl;

import intrepreter.constains.JsonTypeEnum;
import intrepreter.manager.JsonRuleFilter;
import intrepreter.utils.StringUtil;

/**
 * Created by sunlei on 2017/6/9.
 */
public class JsonRuleFilterImpl implements JsonRuleFilter {
    @Override
    public JsonTypeEnum filterRule(String jsonString) {
        //null rule
        if (filterNullRule(jsonString)) {
            return JsonTypeEnum.NULL;
        }
        //num rule
        if (this.filterNumRule(jsonString)) {
            return JsonTypeEnum.NUMBER;
        }
        //boolean rule
        if (this.filterBooleanRule(jsonString)) {
            return JsonTypeEnum.BOOLEAN;
        }
        //array rule
        if (this.filterArrayRule(jsonString)) {
            return JsonTypeEnum.ARRAY;
        }
        //object rule
        if (this.filterObjectRule(jsonString)) {
            return JsonTypeEnum.OBJECT;
        }
        //string rule
        if (this.filterStringRule(jsonString)) {
            return JsonTypeEnum.STRING;
        }
        return JsonTypeEnum.STRING;
    }

    @Override
    public Boolean filterNumRule(String jsonString) {
        if (StringUtil.isNumberStr(jsonString)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean filterStringRule(String jsonString) {
        return true;
    }

    @Override
    public Boolean filterBooleanRule(String jsonString) {
        if (StringUtil.isBoolean(jsonString)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean filterArrayRule(String jsonString) {
        if (jsonString.startsWith("[") && jsonString.endsWith("]")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean filterObjectRule(String jsonString) {
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * null rule
     *
     * @param jsonString
     * @return
     */
    @Override
    public Boolean filterNullRule(String jsonString) {
        if (StringUtil.isEmptyOrNull(jsonString)) {
            return true;
        } else {
            return false;
        }
    }
}
