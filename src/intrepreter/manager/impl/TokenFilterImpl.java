package intrepreter.manager.impl;

import intrepreter.constains.TokenTypeEnum;
import intrepreter.domain.Token;
import intrepreter.manager.TokenFilter;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunlei on 2017/6/13.
 */
public class TokenFilterImpl implements TokenFilter {
    private Reader reader;

    public TokenFilterImpl(Reader reader) {
        this.reader = reader;
    }


    public List<Token> getTokens() {
        List<Token> tokens = new ArrayList<Token>();
        Token token;
        try {
            do {
                token = tokenFilter();
                tokens.add(token);
            } while (token.getTokenType() != TokenTypeEnum.END);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public Token tokenFilter() throws JsonException {
        int c = -1;
        c = getChar();
        //判断是否为最后一个字符
        if (c == -1) {
            return new Token(TokenTypeEnum.END, "-1");
        }
        //判断是否为空
        if (isNull(c)) {
            return new Token(TokenTypeEnum.NULL, "null");
        }
        //判断是否为字符串
        if (c == '"') {
            return convertString();
        }
        //判断是否为数字
        if (isNumber(c)) {
            return readeNum(c);

        }
        //判断是否为[
        if (c == '[') {
            return new Token(TokenTypeEnum.ARRAY_START, "[");
        }
        //判断是否为]
        if (c == ']') {
            return new Token(TokenTypeEnum.ARRAY_END, "]");
        }

        //判断是否为,
        if (c == ',') {
            return new Token(TokenTypeEnum.COLON, ",");
        }
        //判断是否为:
        if (c == ':') {
            return new Token(TokenTypeEnum.SEMICOLON, ":");
        }
        //判断是否为{
        if (c == '{') {
            return new Token(TokenTypeEnum.OBJECT_START, "{");
        }
        //判断是否为}
        if (c == '}') {
            return new Token(TokenTypeEnum.OBJECT_END, "}");
        }
        //判断是否为true
        if (isTrue(c)) {
            return new Token(TokenTypeEnum.BOOLEAN, "true");
        }
        //判断是否为false
        if (isFalse(c)) {
            return new Token(TokenTypeEnum.BOOLEAN, "false");
        }
        throw new JsonException("Invalid JSON input.");
    }

    private boolean isFalse(int c) throws JsonException {
        if (c != 'f') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'a') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'l') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 's') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'e') {
            throw new JsonException("Invalid JSON input.");
        }
        return true;
    }

    private boolean isTrue(int c) throws JsonException {
        if (c != 't') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'r') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'u') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'e') {
            throw new JsonException("Invalid JSON input.");
        }
        return true;
    }


    private Token readeNum(int c) {
        StringBuffer stringBuffer = new StringBuffer();
        while (isDigit(c)) {
            stringBuffer.append((char)c);
            c = getChar();
        }
        return new Token(TokenTypeEnum.NUMBER, stringBuffer.toString());
    }

    private boolean isDigit(int c) {
        if ((c >= '0' && c <= '9') || c == '-' || c == '.') {
            return true;
        } else {
            return false;
        }
    }

    //判断第一位就知道是否是数字

    private boolean isNumber(int c) {
        if ((c >= '0' && c <= '9') || c == '-') {
            return true;
        } else {
            return false;
        }
    }

    private Token convertString() {
        int c = getChar();
        StringBuffer stringBuffer = new StringBuffer();
        while (c != '"') {
            stringBuffer.append((char)c);
            c=getChar();
        }
        return new Token(TokenTypeEnum.STRING, stringBuffer.toString());
    }

    int getChar() {
        int c;
        try {
            c = reader.read();
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 判断是否为空
     *
     * @return
     */
    private boolean isNull(int c) throws JsonException {
        if (c != 'n') {
            return false;
        }
        c = getChar();
        if (c != 'u') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'l') {
            throw new JsonException("Invalid JSON input.");
        }
        c = getChar();
        if (c != 'l') {
            throw new JsonException("Invalid JSON input.");
        }
        return true;
    }

}
