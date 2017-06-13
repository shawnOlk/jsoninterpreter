package intrepreter.domain;

import intrepreter.constains.TokenTypeEnum;

import java.io.Serializable;

/**
 * Created by sunlei on 2017/6/13.
 */
public class Token implements Serializable {
    private TokenTypeEnum tokenType;
    private String tokenStr;

    public Token(TokenTypeEnum tokenType, String tokenStr) {
        this.tokenType = tokenType;
        this.tokenStr = tokenStr;
    }

    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenStr() {
        return tokenStr;
    }

    public void setTokenStr(String tokenStr) {
        this.tokenStr = tokenStr;
    }

    @Override
    public String toString() {
        return tokenStr;
    }
}
