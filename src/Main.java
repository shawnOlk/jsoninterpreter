import com.alibaba.fastjson.JSON;
import intrepreter.constains.JsonTypeEnum;
import intrepreter.domain.JsonObject;
import intrepreter.domain.Token;
import intrepreter.manager.JsonInterpreter;
import intrepreter.manager.TokenFilter;
import intrepreter.manager.impl.JsonInterpreterImpl;
import intrepreter.manager.impl.TokenFilterImpl;
import intrepreter.utils.StringUtil;
import test.School;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Object result = new Object();
        result = getTestObject2();
        String jsonString = JSON.toJSONString(result);
        System.out.println(jsonString);
        TokenFilter tokenFilter = new TokenFilterImpl(new BufferedReader(new StringReader(jsonString)));
        //符号解析
        List<Token> tokens = tokenFilter.getTokens();
        System.out.println(tokens);
        //语法解析
        JsonInterpreter jsonInterpreter = new JsonInterpreterImpl(tokens);
        Map<String, JsonObject> map = jsonInterpreter.interpreterJsonString();

        Set<String> keySet = map.keySet();
        System.out.println("Json Interpreter Map:");
        for (String key : keySet) {
            if (map.get(key).getTypeEnum() != JsonTypeEnum.ARRAY) {
                System.out.println(key + ":" + map.get(key).getJsonValues());
            }
            if (map.get(key).getTypeEnum() == JsonTypeEnum.ARRAY) {
                List<JsonObject> list = (List<JsonObject>) map.get(key).getJsonValues();
                if (StringUtil.isNotEmpty(key)) {//对象中的数组
                    System.out.println(key + ":" + list);
                } else {//JSON就是一个数组
                    System.out.println(list);
                }
            }
        }

    }

    private static Object getTestObject() {
        List<School> result = new ArrayList<School>();
        School school1 = new School();
        School school2 = new School();
        result.add(school1);
        result.add(school2);
        return result;
    }

    private static Object getTestObject2() {
        School school1 = new School();
        return school1;
    }
}
