package intrepreter.manager;

import intrepreter.domain.JsonObejct;

import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public interface JsonInterpreter {
    /**
     * 解释json字符串
     */
    Map<String, JsonObejct> interpreterJsonString(String jsonString);

    /**
     * 扫描json字符串
     */

    Map<String, String> scanJsonString(String jsonString);

    /**
     * 解析数字
     */
    Object praseNum(String jsonString);

    /**
     * 解析字符串
     */
    String praseString(String jsonString);

    /**
     * 解析逻辑值
     *
     * @param jsonString
     * @return
     */
    Boolean praseBoolean(String jsonString);

    /**
     * 解析数组
     *
     * @param jsonString
     * @return
     */
    List<Object> praseArray(String jsonString);

    /**
     * 解析对象
     *
     * @param jsonString
     * @return
     */
    Map<String, JsonObejct> praseObject(String jsonString);

    /**
     * 解析null
     *
     * @param jsonString
     * @return
     */
    Object praseNull(String jsonString);

}
