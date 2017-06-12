package intrepreter.manager;

import intrepreter.constains.JsonTypeEnum;

/**
 * Created by sunlei on 2017/6/9.
 */
public interface JsonRuleFilter {

    /**
     * 过滤规则
     * @param jsonString
     * @return
     */
    JsonTypeEnum filterRule(String jsonString);

    /**
     * 过滤数字规则
     */
    Boolean filterNumRule(String jsonString);

    /**
     * 字符串规则
     */
    Boolean filterStringRule(String jsonString);

    /**
     * 逻辑值规则
     *
     * @param jsonString
     * @return
     */
    Boolean filterBooleanRule(String jsonString);

    /**
     * 判断数组规则
     *
     * @param jsonString
     * @return
     */
    Boolean filterArrayRule(String jsonString);

    /**
     * 判断对象规则
     *
     * @param jsonString
     * @return
     */
    Boolean filterObjectRule(String jsonString);

    /**
     * 判断null规则
     *
     * @param jsonString
     * @return
     */
    Boolean filterNullRule(String jsonString);
}
