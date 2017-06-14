package intrepreter.domain;

import intrepreter.constains.JsonTypeEnum;

import java.io.Serializable;

/**
 * json value bean
 * Created by sunlei on 2017/6/9.
 */
public class JsonObject implements Serializable {
    private JsonTypeEnum typeEnum;
    private Object jsonValues;

    public JsonObject(JsonTypeEnum typeEnum, Object jsonValues) {
        this.typeEnum = typeEnum;
        this.jsonValues = jsonValues;
    }

    public JsonTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(JsonTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public Object getJsonValues() {
        return jsonValues;
    }

    public void setJsonValues(Object jsonValues) {
        this.jsonValues = jsonValues;
    }

    @Override
    public String toString() {
        return ""+ jsonValues
                ;
    }
}
