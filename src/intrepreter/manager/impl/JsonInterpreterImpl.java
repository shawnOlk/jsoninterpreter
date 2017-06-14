package intrepreter.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import intrepreter.constains.JsonTypeEnum;
import intrepreter.constains.TokenTypeEnum;
import intrepreter.domain.JsonObject;
import intrepreter.domain.Token;
import intrepreter.manager.JsonInterpreter;
import test.TestBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public class JsonInterpreterImpl implements JsonInterpreter {

    private List<Token> jsonToken;

    public JsonInterpreterImpl(List<Token> jsonToken) {
        this.jsonToken = jsonToken;
    }

    @Override
    public Map<String, JsonObject> interpreterJsonString() {
        Token token = popToken();
        Map<String, JsonObject> jsonMap = new HashMap<String, JsonObject>();
        while (token.getTokenType() != TokenTypeEnum.END) {
            if (token.getTokenType() == TokenTypeEnum.COLON) {
                continue;
            }
            if (token.getTokenType() == TokenTypeEnum.OBJECT_START) {
                jsonMap = parseObject(jsonMap);
            }
            token = popToken();
        }
        return jsonMap;
    }

    /**
     * 出栈
     *
     * @return
     */
    Token popToken() {
        return jsonToken.remove(0);
    }

    @Override
    public List<Token> scanJsonString(String jsonString) {
        return null;
    }


    @Override
    public JsonObject parseArray() {
        Token currentToken = popToken();
        List<Object> objectArray = new ArrayList<Object>();
        JsonObject jsonObject = new JsonObject(JsonTypeEnum.ARRAY, objectArray);
        do {
            while (currentToken.getTokenType() == TokenTypeEnum.COLON) {
                currentToken = popToken();
            }
            if (currentToken.getTokenType() == TokenTypeEnum.OBJECT_START) {
                Map<String, JsonObject> map = new HashMap<String, JsonObject>();
                map = parseObject(map);
                objectArray.add(map);
            }
            if (isBaseType(currentToken)) {
                objectArray.add(new JsonObject(getJsonType(currentToken.getTokenType()), currentToken.getTokenStr()));
            }

            currentToken = popToken();
        } while (currentToken.getTokenType() != TokenTypeEnum.ARRAY_END);
        return jsonObject;
    }

    @Override
    public Map<String, JsonObject> parseObject(Map<String, JsonObject> jsonMap) {
        Token currentToken = popToken();
        do {

            while (currentToken.getTokenType() != TokenTypeEnum.STRING) {
                currentToken = popToken();
            }
            String key = currentToken.getTokenStr();//key
            currentToken = popToken();//:
            if (currentToken.getTokenType() != TokenTypeEnum.SEMICOLON) {
                throw new JSONException("Invalid JSON input.");
            }
            currentToken = popToken();
            if (isBaseType(currentToken)) {
                jsonMap.put(key, new JsonObject(getJsonType(currentToken.getTokenType()), currentToken.getTokenStr()));
            }
            if (currentToken.getTokenType() == TokenTypeEnum.ARRAY_START) {
                Map<String, JsonObject> map = new HashMap<String, JsonObject>();
                JsonObject jsonObject = parseArray();
                jsonMap.put(key, jsonObject);
            }
            if (currentToken.getTokenType() == TokenTypeEnum.OBJECT_START) {
                Map<String, JsonObject> map = new HashMap<String, JsonObject>();
                map = parseObject(map);
                jsonMap.put(key, new JsonObject(JsonTypeEnum.OBJECT, map));
            }
            currentToken = popToken();
        }
        while (currentToken.getTokenType() != TokenTypeEnum.OBJECT_END);
        return jsonMap;
    }

    private boolean isBaseType(Token currentToken) {
        TokenTypeEnum tokenTypeEnum = currentToken.getTokenType();
        if (tokenTypeEnum == TokenTypeEnum.BOOLEAN || tokenTypeEnum == TokenTypeEnum.NUMBER || tokenTypeEnum == TokenTypeEnum.NULL | tokenTypeEnum == TokenTypeEnum.STRING) {
            return true;
        }
        return false;
    }

    private JsonTypeEnum getJsonType(TokenTypeEnum tokenType) {
        switch (tokenType) {
            case NULL:
                return JsonTypeEnum.NULL;
            case NUMBER:
                return JsonTypeEnum.NUMBER;
            case STRING:
                return JsonTypeEnum.STRING;
            case BOOLEAN:
                return JsonTypeEnum.BOOLEAN;
        }
        return null;
    }


    public static void main(String[] args) {
        TestBean result = new TestBean();
        String jsonString = JSON.toJSONString(result);

        System.out.println(jsonString);

    }
}
