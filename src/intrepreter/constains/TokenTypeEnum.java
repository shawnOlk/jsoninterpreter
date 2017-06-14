package intrepreter.constains;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/13.
 */
public enum TokenTypeEnum {
    OBJECT_START(1, "{"),
    OBJECT_END(2, "}"),
    ARRAY_START(3, "["),
    ARRAY_END(4, "]"),
    COLON(5, ","),
    END(6, "结束"),
    SEMICOLON(7, ":"),
    STRING(8,"字符串"),
    NUMBER(9, "数字"),
    NULL(10, "NULL"),
    BOOLEAN(11,"布尔");

    private static final Map<Integer, TokenTypeEnum> keyMap;

    static {
        keyMap = new HashMap<Integer, TokenTypeEnum>();
        for (TokenTypeEnum item : TokenTypeEnum.values()) {
            keyMap.put(item.getCode(), item);
        }
    }

    private static TokenTypeEnum get(Integer code) {
        return keyMap.get(code);
    }

    private Integer code;
    private String msg;

    TokenTypeEnum(Integer code, String msg) {
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
