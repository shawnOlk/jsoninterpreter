package intrepreter.manager;

import intrepreter.domain.Token;

import java.util.List;

/**
 * Created by sunlei on 2017/6/13.
 */
public interface TokenFilter {
    List<Token> getTokens();
}
