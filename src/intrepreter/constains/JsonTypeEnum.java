package intrepreter.constains;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public enum JsonTypeEnum {
    NUMBER(1, "数字"),
    STRING(2, "字符串"),
    BOOLEAN(3, "逻辑值"),
    ARRAY(4, "数组"),
    OBJECT(5, "对象"),
    NULL(6, "null");

    private static final Map<Integer, JsonTypeEnum> keyMap;

    static {
        keyMap = new HashMap<Integer, JsonTypeEnum>();
        for (JsonTypeEnum item : JsonTypeEnum.values()) {
            keyMap.put(item.getCode(), item);
        }
    }

    private static JsonTypeEnum get(Integer code) {
        return keyMap.get(code);
    }

    private Integer code;
    private String msg;

    JsonTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
