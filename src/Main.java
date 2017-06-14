import com.alibaba.fastjson.JSON;
import intrepreter.constains.JsonTypeEnum;
import intrepreter.domain.JsonObject;
import intrepreter.domain.Token;
import intrepreter.manager.JsonInterpreter;
import intrepreter.manager.TokenFilter;
import intrepreter.manager.impl.JsonInterpreterImpl;
import intrepreter.manager.impl.TokenFilterImpl;
import test.School;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        School result = new School();
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
            if(map.get(key).getTypeEnum()!= JsonTypeEnum.ARRAY) {
                System.out.println(key + ":" + map.get(key).getJsonValues());
            }
            if(map.get(key).getTypeEnum()== JsonTypeEnum.ARRAY) {
                List<JsonObject> list=( List<JsonObject>)map.get(key).getJsonValues();
                System.out.println(key + ":" +list);
            }
        }

    }
}
