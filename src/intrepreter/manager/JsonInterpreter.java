package intrepreter.manager;

import intrepreter.domain.JsonObject;
import intrepreter.domain.Token;

import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public interface JsonInterpreter {
    /**
     * 解释json字符串
     */
    Map<String, JsonObject> interpreterJsonString();

    /**
     * 扫描string
     * @param jsonString
     * @return
     */
    List<Token> scanJsonString(String jsonString);

    /**
     * 解析数组
     * @return
     */
    JsonObject parseArray();

    /**
     * 解析对象
     * @return
     */
    Map<String, JsonObject> parseObject(Map<String, JsonObject> jsonMap);


}
