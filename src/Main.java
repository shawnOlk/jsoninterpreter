import com.alibaba.fastjson.JSON;
import intrepreter.domain.Token;
import intrepreter.manager.TokenFilter;
import intrepreter.manager.impl.TokenFilterImpl;
import test.TestBean;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TestBean result = new TestBean();
        String jsonString = JSON.toJSONString(result);
        System.out.println(jsonString);
        TokenFilter tokenFilter = new TokenFilterImpl(new BufferedReader(new StringReader(jsonString)));
        List<Token> tokens = tokenFilter.getTokens();

        System.out.println(tokens);
    }
}
