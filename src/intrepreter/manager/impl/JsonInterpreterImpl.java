package intrepreter.manager.impl;

import intrepreter.manager.JsonInterpreter;
import intrepreter.domain.JsonObejct;

import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2017/6/9.
 */
public class JsonInterpreterImpl implements JsonInterpreter {
    @Override
    public Map<String, JsonObejct> interpreterJsonString(String jsonString) {
        return null;
    }

    @Override
    public Map<String, String> scanJsonString(String jsonString) {
        return null;
    }

    @Override
    public Object praseNum(String jsonString) {
        return Double.parseDouble(jsonString);
    }

    @Override
    public String praseString(String jsonString) {
        return jsonString;
    }

    @Override
    public Boolean praseBoolean(String jsonString) {
        return Boolean.valueOf(jsonString);
    }

    @Override
    public List<Object> praseArray(String jsonString) {
        return null;
    }

    @Override
    public Map<String, JsonObejct> praseObject(String jsonString) {
        return null;
    }

    @Override
    public Object praseNull(String jsonString) {
        return null;
    }
}
