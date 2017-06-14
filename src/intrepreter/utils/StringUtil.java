package intrepreter.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String URL_REGEX = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    private static final String HTTP_URL_REGEX = "((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    /**
     * <p>
     * The maximum size to which the padding constant(s) can expand.
     * </p>
     */
    private static final int PAD_LIMIT = 8192;
    /**
     * 小数正在表达式，具体小数位置需要在使用时指定
     * String.format()
     */
    private static final String NUMBER_DECIMAL_REGEX_NEED_FORMAT = "^(([1-9]\\d*)|(0))(\\.\\d{1,%s})?$";

    /**
     * 会员折扣小数格式化，保留两位小数
     */
    public static final String DECIMAL_FORMAT_PATTERN_TWO = "##.##";

    public static final String SEPARATOR = ",";

    /**
     * 组装集合成为 半角","连接的字符串 -例如 a,b,c
     *
     * @param collection
     * @return
     */
    public static String collectionToStrings(Collection<? extends Object> collection) {
        StringBuilder builder = new StringBuilder();
        if (collection != null && collection.size() > 0) {
            for (Iterator<? extends Object> ite = collection.iterator(); ite.hasNext(); ) {
                builder.append(ite.next());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);//Remove the last ","
        }
        return builder.toString();
    }

    /**
     * 将一个list组装成字符串放在sql in中使用
     *
     * @param idList
     * @return
     */
    public static String makeStrCollection(List idList) {

        if (idList == null || idList.size() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (; i < idList.size() - 1; i++) {
            builder.append(idList.get(i)).append(",");
        }
        builder.append(idList.get(i));

        return builder.toString();
    }

    /**
     * 替换in语句，动态组装or查询条件
     *
     * @param num
     * @param unit
     * @return
     */
    public static String formateOrCondition(int num, final String unit) {

        if (num <= 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(num * (unit.length() + 2));

        builder.append(unit);

        for (int i = 1; i < num; i++) {
            builder.append("or");
            builder.append(unit);
        }

        return builder.toString();
    }

    /**
     * 将一个规格字符串变成一个64位long型 1|2|3| ----> 111 = 7
     *
     * @param input
     * @param delim
     * @return
     */
    public static long transToLong(String input, final String delim) {

        String tmpStr = input;

        long result = 0;

        if (tmpStr == null || tmpStr.length() == 0) {
            return result;
        }

        String[] delimal = tmpStr.split(delim);

        if (delimal.length > 63) {
            return result;
        }

        int charInt = 0;

        for (int i = 0; i < delimal.length; i++) {
            try {
                charInt = Integer.parseInt(delimal[i]);
            } catch (Exception e) {
                charInt = 0;
            }

            if (charInt > 0) {
                result += (1 << i);
            }
        }

        return result;
    }

    /**
     * 将数字转换为百分数表示
     *
     * @param num      double 需要表示为百分数的数字
     * @param fraction int 百分数中的小数位
     * @return String 代表百分数的字符串
     */
    public static String getPercent(double num, int fraction) {
        NumberFormat fmt = NumberFormat.getPercentInstance();

        fmt.setMaximumFractionDigits(fraction);
        return fmt.format(num);
    }

    /**
     * 转换数值双精度型保留2位小数
     *
     * @param number  要转换的数值
     * @param formate 格式串 "0.00" 返回转换后的数值
     */
    public static String convNumber(double number, String formate) {
        DecimalFormat df = new DecimalFormat(formate);
        return df.format(number);
    }

    /**
     * 转换数值双精度型保留2位小数
     * 不显示多余的0
     *
     * @param number 要转换的数值
     */
    public static String convNumber(double number) {
        DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT_PATTERN_TWO);
        return df.format(number);
    }

    public static boolean isTraditionalChineseCharacter(char c, boolean checkGbk) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if (!Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)
                && !Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                .equals(block)
                && !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                .equals(block)) {
            return false;
        }
        if (checkGbk) {
            try {
                String s = "" + c;
                return s.equals(new String(s.getBytes("GBK"), "GBK"));
            } catch (UnsupportedEncodingException e) {
                return false;
            }
        }

        return true;
    }

    public static boolean isGBK(String input) {
        try {
            if (input.equals(new String(input.getBytes("GBK"), "GBK"))) {
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static boolean validBeidouGbkStr(String input,
                                            final boolean checkGbk, final int minLength, final int maxLength) {

        // 对长度有个预先判断
        if ((minLength > -1) && (input.length() < minLength)) {
            return false;
        } else if ((maxLength > -1) && (input.length() > maxLength)) {
            return false;
        }

        // 验证字符合法性和特殊长度要求
        char[] ch = input.toCharArray();

        int length = 0;

        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isNeedAlph(c)) {
                length += 1;
            } else if (isTraditionalChineseCharacter(c, checkGbk)) {
                length += 2;
            } else {
                return false;// 是否为合法字符集
            }
        }

        if ((minLength > -1) && (length < minLength)) {
            return false;
        } else if ((maxLength > -1) && (length > maxLength)) {// bug,怎么为true
            return false;
        }

        return true;
    }

    private static boolean isNeedAlph(char c) {

        if (c >= 'a' && c <= 'z') {
            return true;
        } else if (c >= 'A' && c <= 'Z') {
            return true;
        } else if (c >= '0' && c <= '9') {
            return true;
        } else if (c == '-') {
            return true;
        } else if (c == '_') {
            return true;
        }

        return false;
    }

    public static boolean isLatinCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if (!Character.UnicodeBlock.BASIC_LATIN.equals(block)) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(isBoolean("false"));
    }

    /**
     * 将list转化格式化为string表示的集合类型 puyuda add @ 20061120
     *
     * @param input
     * @param delimiters
     * @param trimTokens
     * @param ignoreEmptyTokens
     * @return
     */
    public static String tokenizeListToStringNo(List input, String delimiters,
                                                boolean trimTokens, boolean ignoreEmptyTokens) {
        StringBuffer result = new StringBuffer();
        Iterator inputIt = input.iterator();
        while (inputIt.hasNext()) {
            String token = inputIt.next().toString();
            if (trimTokens) {
                token = token.trim();
            }
            if (!(ignoreEmptyTokens && token.length() == 0)) {
                result.append(token);
                result.append(delimiters);
            }
        }
        result.replace(result.length() - 1, result.length(), "");
        return result.toString();
    }

    /**
     * 将String数组转化格式化为String对象 puyuda add @ 20061120
     *
     * @param s
     * @param delimiters
     * @param trimTokens
     * @param ignoreEmptyTokens
     * @return
     */
    public static String tokenizeStringToString(String[] s, String delimiters,
                                                boolean trimTokens, boolean ignoreEmptyTokens) {
        StringBuffer result = new StringBuffer();
        if (delimiters == null) {
            delimiters = ",";
        }
        if (s != null && s.length > 0) {
            for (int i = 0; i < s.length; i++) {
                result.append(s[i]);
                result.append(delimiters);
            }
            result.replace(result.length() - delimiters.length(), result
                    .length(), "");
        }
        return result.toString();
    }

    /**
     * 将String(不带括号的集合类型)转化格式化为List puyuda add @ 20061120
     *
     * @param s
     * @param delimiters
     * @param trimTokens
     * @param ignoreEmptyTokens
     * @return
     */
    public static List tokenizeStringToList(String s, String delimiters,
                                            boolean trimTokens, boolean ignoreEmptyTokens) {
        List tokens = new ArrayList();
        if (s != null && s.length() > 0) {
            StringTokenizer st = new StringTokenizer(s, delimiters);

            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (trimTokens) {
                    token = token.trim();
                }
                if (!(ignoreEmptyTokens && token.length() == 0)) {
                    tokens.add(token);
                }
            }
        }
        return tokens;
    }

    // //////////////////////////////////////////////////////////////////////////
    // ////////////

    /**
     * 将数字转换为百分数表示（百分数中2位小数）
     *
     * @param num double 需要表示为百分数的数字
     * @return String 代表百分数的字符串
     */
    public static String getPercent(double num) {
        NumberFormat fmt = NumberFormat.getPercentInstance();

        fmt.setMaximumFractionDigits(2);
        return fmt.format(num);
    }

    /**
     * 将数字转换为只保留fraction位小数位
     *
     * @param num      double 需要转化的数字
     * @param fraction int 小数位
     * @return String
     */
    public static String getFractionDigits(double num, int fraction) {
        NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMaximumFractionDigits(fraction);
        return fmt.format(num);
    }

    public static String getMoney(double num) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.CHINA);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(num);
    }

    public static char getCharFromEnd(String inString, int index) {
        return inString.charAt(inString.length() - index - 1);
    }

    public static String getStringFromEnd(String inString, int length) {
        return inString.substring(inString.length() - length);
    }

    /**
     * @param inString String
     * @param index    int 从尾部计量的index
     * @param newChar  char
     * @return String
     */
    public static String replace(String inString, int index, char newChar) {
        if (inString == null) {
            return null;
        }

        int len = inString.length();
        if (index < 0 || index >= len) {
            return inString;
        }

        int pos = len - index - 1;

        StringBuffer sbuf = new StringBuffer();
        sbuf.append(inString.substring(0, pos));
        sbuf.append(newChar);

        sbuf.append(inString.substring(pos + 1));

        return sbuf.toString();

    }

    /**
     * 将String中出现某一子串之处用另一子串代替 another string.
     *
     * @param inString   进行处理的字符串
     * @param oldPattern 被替换的子串
     * @param newPattern 替换的子串
     * @return String 被替换后的字符串
     */
    public static String replace(String inString, String oldPattern,
                                 String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();
        // output StringBuffer we'll build up
        int pos = 0; // Our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));

        // remember to append any characters to the right of a match
        return sbuf.toString();
    }

    /**
     * 输入文件名，得到文件的扩展名
     *
     * @param filename String 文件名
     * @return String 文件扩展名
     */
    public static String getFileSuffix(String filename) {
        if (filename == null) {
            return null;
        }
        if (filename.lastIndexOf(".") == -1
                || filename.lastIndexOf('.') == (filename.length() - 1))
            return "";
        StringTokenizer token = new StringTokenizer(filename, ".");
        String filetype = "";
        while (token.hasMoreTokens()) {
            filetype = token.nextToken();
        }
        return filetype;
    }

    /**
     * 文件的扩展名的判断
     *
     * @param suffix String 文件名
     * @return String 文件扩展名
     */
    public static Boolean judgeSuffix(String suffix) {
        if (suffix == null) {
            return null;
        }
        Set<String> suffixSet = new HashSet<String>();
        suffixSet.add("bat");
        suffixSet.add("cmd");
        suffixSet.add("exe");
        if (suffixSet.contains(suffix)) {
            return true;
        }
        return false;
    }

    /*
     * 从 ISO8859_1 到 GB2312的转换
     */
    public static final String toChinese(String strVal) {
        try {
            if (strVal == null) {
                return null;
            } else {
                return new String(strVal.getBytes("ISO8859_1"), "GB2312");
            }
        } catch (Exception exp) {
            return null;
        }
    }

    /**
     * 从 ISO8859_1 到 utf-8的转换
     */
    public static final String toUTF8Chinese(String strVal) {
        try {
            if (strVal == null) {
                return null;
            } else {
                return new String(strVal.getBytes("ISO8859_1"), "utf-8");
            }
        } catch (Exception exp) {
            return null;
        }
    }

    public static String iptoStr(String ip) throws Exception {
        String ip_tmp = "";
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");
        String token_tmp = "";

        try {
            while (tokenizer.hasMoreTokens()) {
                token_tmp = tokenizer.nextToken();
                if (token_tmp.length() == 3) {
                    ip_tmp = ip_tmp + token_tmp;

                }
                if (token_tmp.length() == 2) {
                    ip_tmp = ip_tmp + "0" + token_tmp;

                }
                if (token_tmp.length() == 1) {
                    ip_tmp = ip_tmp + "00" + token_tmp;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ip_tmp;
    }

    /**
     * 将一个标题缩短显示
     *
     * @param title          String
     * @param shortStrLength int
     * @return String
     */
    public static String getShortLinkTitle(String title, int shortStrLength) {
        String tail = "...";
        if (title.length() < shortStrLength + tail.length()) {
            return title;
        }
        return title.substring(0, shortStrLength) + tail;
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     *
     * @param s                 the String to tokenize
     * @param delimiters        the delimiter characters, assembled as String
     * @param trimTokens        trim the tokens via String.trim
     * @param ignoreEmptyTokens omit empty tokens from the result array
     * @return an array of the tokens
     * @see StringTokenizer
     * @see String#trim
     */
    public static String[] tokenizeToStringArray(String s, String delimiters,
                                                 boolean trimTokens, boolean ignoreEmptyTokens) {
        StringTokenizer st = new StringTokenizer(s, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!(ignoreEmptyTokens && token.length() == 0)) {
                tokens.add(token);
            }
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    /**
     * 将字符串转化成Long型数组
     *
     * @param s          the String to tokenize
     * @param delimiters the delimiter characters, assembled as String
     * @return an array of the tokens
     * @see StringTokenizer
     * @see String#trim
     */
    public static Long[] tokenizeToLongArray(String s, String delimiters) {
        if (s == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, delimiters);
        List<Long> tokens = new ArrayList<Long>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token.trim();
            if (isNumberStr(token)) {
                tokens.add(Long.valueOf(token));
            }
        }
        return tokens.toArray(new Long[tokens.size()]);
    }

    /**
     * 将字符串转化成Long型数组
     *
     * @param s          the String to tokenize
     * @param delimiters the delimiter characters, assembled as String
     * @return an array of the tokens
     * @see StringTokenizer
     * @see String#trim
     */
    public static List<Long> tokenizeToLongList(String s, String delimiters) {
        if (s == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, delimiters);
        List<Long> tokens = new ArrayList<Long>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token.trim();
            if (isNumberStr(token)) {
                tokens.add(Long.valueOf(token));
            }
        }
        return tokens;
    }

    /**
     * 将list转化格式化为string表示的集合类型 puyuda add @ 20061120
     *
     * @param input
     * @param delimiters
     * @param trimTokens
     * @param ignoreEmptyTokens
     * @return
     */
    public static String tokenizeListToString(List input, String delimiters,
                                              boolean trimTokens, boolean ignoreEmptyTokens) {
        StringBuffer result = new StringBuffer();
        result.append("(");
        Iterator inputIt = input.iterator();
        while (inputIt.hasNext()) {
            Object o = inputIt.next();
            if (o == null) continue;
            String token = o.toString();
            if (trimTokens) {
                token = token.trim();
            }
            if (!(ignoreEmptyTokens && token.length() == 0)) {
                result.append(token);
                result.append(delimiters);
            }
        }
        result.replace(result.length() - 1, result.length(), ")");
        return result.toString();
    }

    public static String tokenizeListToStringWithNoBracket(List input,
                                                           String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        StringBuffer result = new StringBuffer();
        result.append("");
        Iterator inputIt = input.iterator();
        while (inputIt.hasNext()) {
            String token = inputIt.next().toString();
            if (trimTokens) {
                token = token.trim();
            }
            if (!(ignoreEmptyTokens && token.length() == 0)) {
                result.append(token);
                result.append(delimiters);
            }
        }
        result.replace(result.length() - 1, result.length(), "");
        return result.toString();
    }

    /**
     * 去除字符串内ASCII码从1到31的字符
     *
     * @param inputString String
     * @return String
     */
    public static String checkString(String inputString) {
        if (inputString == null) {
            return null;
        }
        String model = "[\\x01-\\x1f]";
        // String model = "[^a-z]";
        // 检测是否包含非法字符
        Pattern p = Pattern.compile(model);
        Matcher m = p.matcher(inputString);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        boolean deletedIllegalChars = false;
        while (result) {
            // 如果找到了非法字符那么就设下标记
            deletedIllegalChars = true;
            // 如果里面包含非法字符，那么就把他们消去，加到SB里面
            m.appendReplacement(sb, "");
            System.out.println("修改类似: " + sb.toString());
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();

    }

    /**
     * 判断字符串是否可以转换为一个整形
     *
     * @param inputString String
     * @return boolean
     */
    public static boolean isNumber(String inputString) {
        // NumberFormat nf = NumberFormat.getInstance();
        // try{
        // nf.parse(inputString);
        // }catch(ParseException ps){
        // return false;
        // }
        // return true;
        try {
            new Double(inputString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * puyuda add @ 20061117 将字符串转换为一个Long
     *
     * @param inputString String
     * @return Long
     */
    public static Long parseLong(String inputString) {
        if (isNumber(inputString)) {

            return new BigDecimal(inputString).longValue();
        } else {
            return null;
        }
    }

    public static Long toLong(String inputString) {
        if (isNumber(inputString)) {
            return new BigDecimal(inputString).longValue();
        } else {
            return null;
        }
    }

    /**
     * puyuda add @ 20061117 将一个Long转化为10位的字符串
     *
     * @param input Long
     * @return String
     */
    public static String longToString(Long input) {
        String base = "0000000000";
        if (input == null) {
            return base;
        }
        String in = input.toString();
        in = base + in;
        int len = in.length();
        return in.substring(len - 10, len);
    }

    /**
     * Wine.Ye add @ 20070905 将字符串转换为一个Integer
     *
     * @param inputString String
     * @return Integer
     */
    public static Integer parseInteger(String inputString) {
        if (isNumber(inputString)) {
            return Double.valueOf(inputString).intValue();
        } else {
            return null;
        }
    }

    /**
     * Wine.Ye add @ 20070905 将字符串转换为一个Integer
     *
     * @param inputNum String
     * @return Integer
     */
    public static Integer parseInteger(Number inputNum) {
        if (inputNum != null) {
            return inputNum.intValue();
        } else {
            return null;
        }
    }

    public static Integer toInteger(String inputString) {
        if (isNumber(inputString)) {
            return Double.valueOf(inputString).intValue();
        } else {
            return null;
        }
    }

    /**
     * Wine.Ye add @ 20070905 将字符串转换为一个Integer
     *
     * @param inputString String
     * @return Integer
     */
    public static Integer parseInt(String inputString) {

        return parseInteger(inputString);
    }

    public static boolean isAllEqual(List objectList) {
        Iterator it = objectList.iterator();
        Object temp = it.next();
        while (it.hasNext()) {
            Object ob = it.next();
            if (!temp.equals(ob))
                return false;
        }
        return true;
    }

    public static long getMaxLong(List objectList) {
        Iterator it = objectList.iterator();
        long temp = ((Long) it.next()).longValue();
        while (it.hasNext()) {
            long ob = ((Long) it.next()).longValue();
            if (ob > temp)
                temp = ob;

        }
        return temp;
    }

    public static String encodeGBK(String input)
            throws UnsupportedEncodingException {
        String output = input;
        if (!System.getProperty("file.encoding").equals("GBK")) {

            output = new String(input.getBytes("ISO8859_1"), "GBK");

        }
        return output;
    }

    public static String encodeISO(String input)
            throws UnsupportedEncodingException {
        String output = input;
        if (!System.getProperty("file.encoding").equals("GBK")) {

            output = new String(input.getBytes("GBK"), "ISO8859_1");

        }
        return output;
    }

    /**
     * 判断字符串str是否只含有字母和数字
     *
     * @param str
     * @return
     */
    public static boolean isLiteralAndNum(String str) {
        if (str == null) {
            return false;
        }
        String model = "[a-zA-Z0-9]*";
        // 检测是否包含非法字符
        Pattern p = Pattern.compile(model);
        Matcher m = p.matcher(str);
        return m.matches();

    }

    /**
     * 转换模糊查询中含有的通配符
     *
     * @param matching 查询字符串
     * @return 通配符转义后的字符串
     */
    public static String decodeSqlMatching(String matching) {
        String retStr = null;
        retStr = StringUtil.replace(matching, "%", "\\%");
        retStr = StringUtil.replace(retStr, "_", "\\_");
        return retStr;
    }

    /**
     * 对于每一个 url，将其扩展成4个，如：http://www.baidu.com , http://baidu.com,
     * www.baidu.com , baidu.com 扩大匹配的准确度
     *
     * @param url 待扩展urls
     * @return 扩展的urls by zhangpeng 20060707
     */
    public static List formateUrl(String url) {
        List resultList = new ArrayList();

        String tmpUrl = url;
        String flageHead1 = "http://";
        String flageHead2 = "www.";

        String centUrl = "";
        String urlWithHead1 = "";
        String urlWithHead2 = "";
        String urlWithHead12 = "";

        if (tmpUrl.indexOf(flageHead1) == 0) {
            if (tmpUrl.indexOf(flageHead1 + flageHead2) == 0) {
                centUrl = tmpUrl.substring(flageHead1.length()
                        + flageHead2.length());
            } else {
                centUrl = tmpUrl.substring(flageHead1.length());
            }
        } else if (tmpUrl.indexOf(flageHead2) == 0) {
            centUrl = tmpUrl.substring(flageHead2.length());
        } else {
            centUrl = tmpUrl;
        }

        urlWithHead1 = flageHead1 + centUrl;
        urlWithHead2 = flageHead2 + centUrl;
        urlWithHead12 = flageHead1 + flageHead2 + centUrl;

        resultList.add(urlWithHead12);
        resultList.add(urlWithHead1);
        resultList.add(urlWithHead2);
        resultList.add(centUrl);

        return resultList;
    }

    /**
     * 随机生成密码，但要符合以下用户密码规则：
     * 长度不能少于6位，不能多于10位；必须同时包含大写英文字母、小写英文字母、阿拉伯数字；不能与前三次历史密码相同，不能与用户名相同（区分大小写）。
     *
     * @return 密码 by maofenghua 20061208
     */
    public static String generatRandomPassword() {
        String passwd = "";
        Random random = new Random();
        char letter;
        String rand;
        for (int i = 0; i < 2; i++) {
            rand = String.valueOf(random.nextInt(9));
            passwd = passwd + rand;
        }
        for (int i = 0; i < 2; i++) {
            letter = (char) (random.nextInt(26) + 65);
            rand = String.valueOf(letter);
            passwd = passwd + rand;
        }
        for (int i = 0; i < 2; i++) {
            letter = (char) (random.nextInt(26) + 97);
            rand = String.valueOf(letter);
            passwd = passwd + rand;
        }
        return passwd;
    }

    public static String filterHTML(String src) {
        return src.replaceAll("<.+?>", "");
    }

    /**
     * added by Savvy Shi @ 20080507 从HTML字符串中截取不多于指定长度的纯文本
     *
     * @param src       待处理的HTML文本
     * @param maxLength 需要从HTML文本中截取的纯文本最大长度
     * @return 被抽取的纯文本，其中不包含任何的HTML标签和转义字符
     */
    public static String filterHTML(String src, int maxLength) {
        if (src == null)
            return null;
        else {
            if (maxLength <= 0)
                return "";

            // 去掉所有的HTML标签
            src = Pattern.compile("<[^(<|>)]*>").matcher(src).replaceAll("");

            // 一般情况下，HTML的转义字符都是以&开头，以;结尾
            Matcher matcher = Pattern.compile("&[^(&|;)]+;").matcher(src);

            maxLength = maxLength > src.length() ? src.length() : maxLength;
            int endingPos = 0; // 应该截取的位置
            int groupLengthSum = 0; // 截取结果中转义字符的字面长度
            int groupCount = 0; // 截取结果中转义字符的个数，实际上也是转义后的字符长度
            do {
                // 找到转义符的起始位置
                int nextComparingPos = matcher.find() ? matcher.start() : (src
                        .length());

                // 从0到nextComparingPos，转义后的字符长度
                int transfferedLength = nextComparingPos - groupLengthSum
                        + groupCount;

                // 如果转义后的字符长度超过了需求长度，则进行截取并返回结果
                if (transfferedLength >= maxLength) {
                    endingPos = nextComparingPos
                            - (transfferedLength - maxLength);
                    break;
                } else {
                    // 未超过需求长度，且未达到src结尾，则增加转义字符的字面长度，和转义字符的个数
                    if (!matcher.hitEnd()) {
                        groupLengthSum += matcher.group().length();
                        groupCount++;
                    } else {
                        endingPos = src.length();
                        break;
                    }
                }
            } while (true);
            return src.substring(0, endingPos);
        }
    }

    public static String trimString(String src) {
        return src == null ? "" : src.trim();
    }

    /**
     * WineYe add @ 20071226 将字符串转换为一个Double
     *
     * @param inputString String
     * @return Double
     */
    public static Double parseDouble(String inputString) {
        if (isNumber(inputString)) {
            return Double.valueOf(inputString);
        } else {
            return null;
        }
    }

    public static Double toDouble(String inputString) {
        if (isNumber(inputString)) {
            return Double.valueOf(inputString);
        } else {
            return null;
        }
    }

    /**
     * L.Wang add @ 20090326 将字符串转换为一个Float
     *
     * @param inputString
     * @return Float(转换出现异常返回null)
     */
    public static Float parseFloat(String inputString) {
        if (isNumber(inputString)) {
            return Float.valueOf(inputString);
        } else {
            return null;
        }
    }

    public static Float toFloat(String inputString) {
        if (isNumber(inputString)) {
            return Float.valueOf(inputString);
        } else {
            return null;
        }
    }

    // copy from ShowQuestionDetailAction copy by Wine.Ye on 20080122
    public static String changeLinedText(String str) {
        String ss = str;
        String s1 = "\r\n";
        return ss.replaceAll(s1, "<br>");
    }

    /**
     * 校验字符串是否合法，合法条件：不是null & trim后不等于“”
     *
     * @param target String
     * @return boolean
     * @author Administrator
     * @date 2008-12-22
     */
    public static boolean checkStr(String target) {
        return target != null && !target.trim().equals("");
    }

    /**
     * 构建IN查询语句片段，如果isChar=true，那么集合中的每一项会带有“'”。
     *
     * @param str
     * @param isChar 是否是构建字符型的IN语句
     * @param split  字符串分割符
     * @return 2008-10-17
     */
    public static String createInStr(String str, boolean isChar, String split) {
        return createInOrNotinStr(str, "IN", isChar, split);
    }

    /**
     * 构建NOT IN查询语句片段，如果isChar=true，那么集合中的每一项会带有“'”
     *
     * @param str
     * @param isChar 是否是构建字符型的IN语句
     * @param split  字符串分割符
     * @return 2008-10-17
     */
    public static String createNotInStr(String str, boolean isChar, String split) {
        return createInOrNotinStr(str, "NOT IN", isChar, split);
    }

    private static String createInOrNotinStr(String str, String sign,
                                             boolean isChar, String split) {
        str = str.replaceAll(" ", "");
        str = str.replaceAll("\t", "");
        String s = str;
        if (isChar) {
            s = formatString(s, split);
        }
        StringBuilder sb = new StringBuilder(sign);
        sb.append("(");
        if (",".equals(split)) {
            sb.append(s);
        } else {
            String[] temp = s.split(split);
            s = StringUtil.objArrToStr(temp);
            sb.append(s);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * format String: "a,b,c"--->"'a','b','c'" or "a;b"--->"'a','b'"
     *
     * @param target
     * @param split
     * @return
     */
    public static String formatString(String target, String split) {
        if (target == null || target.length() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        String[] temp = target.split(split);
        for (int i = 0; i < temp.length; i++) {
            sb.append("'");
            sb.append(temp[i]);
            sb.append("'");
            sb.append(",");
        }
        trimEndSeparate(sb, ",");
        return sb.toString();
    }

    /**
     * 把对象数组变成以“，”隔开的字符串
     *
     * @param arr
     * @return String
     * @author Jiangwenfeng 2008-10-17
     */
    public static String objArrToStr(Object[] arr) {
        if (arr == null || arr.length <= 0) {
            return null;
        }
        return objArrToStr(arr, ",");
    }

    public static String objArrToStr(Object[] arr, String split) {
        if (arr == null || arr.length <= 0) {
            return null;
        }
        if (split == null) {
            split = ",";
        }
        String temp = Arrays.toString(arr);
        temp = temp.replaceAll(", ", split);
        return temp.substring(1, temp.length() - 1);
    }

    /**
     * @param str
     * @param separate
     */
    public static void trimEndSeparate(StringBuffer str, String separate) {
        if (str == null || separate == null || str.length() == 0
                || separate.length() == 0) {
            return;
        }

        if (str.length() >= separate.length()) {
            str.setLength(str.length() - separate.length());
        }
    }

    /**
     * @param str
     * @param separate
     */
    public static void trimEndSeparate(StringBuilder str, String separate) {
        if (str == null || separate == null || str.length() == 0
                || separate.length() == 0) {
            return;
        }

        if (str.length() >= separate.length()) {
            str.setLength(str.length() - separate.length());
        }
    }

    public static boolean checkKeyWords(String keywords) {
        if (!checkStr(keywords)) {
            return false;
        } else if (keywords.replaceAll(",", "").trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * 全角转半角 add by lijin
     */
    public static String transferToDBC(String inStr)
            throws UnsupportedEncodingException {
        StringBuffer result = new StringBuffer();
        String tempStr = "";
        byte[] b = null;
        String codeType = "unicode";
        if (inStr != null && inStr.length() > 0) {
            for (int i = 0; i < inStr.length(); i++) {
                tempStr = inStr.substring(i, i + 1);
                b = tempStr.getBytes(codeType);
                if (b != null) {
                    if (b[3] == -1) {
                        b[2] = (byte) (b[2] + 32);
                        b[3] = 0;
                        result.append(new String(b, codeType));
                    } else {
                        result.append(tempStr);
                    }
                }
            }
        }
        return result.toString();

    }

    public static String formatKeyWords(String keywords)
            throws UnsupportedEncodingException {
        String keywordstr = transferToDBC(keywords);
        Set keywordSet = new TreeSet(Arrays.asList(keywordstr.split(",")));
        keywordstr = tokenizeStringToString((String[]) keywordSet
                .toArray(new String[]{}), ",", false, false);
        return keywordstr;

    }

    public static String getFilename(String filepath) {
        String filename = null;
        if (filepath == null || filepath.trim().equals("")) {
            return "";
        } else if (filepath.lastIndexOf(File.separator) != -1) {
            filename = filepath
                    .substring(filepath.lastIndexOf(File.separator) + 1,
                            filepath.length());
            return filename;
        }
        return filename;
    }

    public static boolean isNumberStr(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }
        String numstr = "0123456789";
        char[] strchars = str.toCharArray();
        for (int i = 0; i < strchars.length; i++) {
            if (i == 0 && (strchars[0] == '-' || strchars[0] == '+')) {
                // 忽略第一个负号或正号
                continue;
            }
            if (numstr.indexOf(strchars[i]) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否是正整数
     *
     * @param str
     * @return
     */
    public static boolean isPositiveInteger(String str) {
        if (null == str) {
            return false;
        }
        String regExp = "^[0-9]*[1-9][0-9]*$";
        Pattern p1 = Pattern.compile(regExp);
        Matcher m1 = p1.matcher(str);
        if (m1.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 比较两个字符串按指定分隔符分割后的结果是否相等（如"a,b"、"b,a"、"a,,b"、"a,b"都相等）<br>
     * add by Mazhy
     *
     * @param str1   String 字符串参数1
     * @param delim1 String 字符串参数1的分隔符
     * @param str2   String 字符串参数2
     * @param delim2 String 字符串参数2的分隔符
     * @return true-两个字符串都非空，且各自分割后所得到的结果相同（不限制子字符串的顺序），false-其他
     */
    public static boolean equals(String str1, String delim1, String str2,
                                 String delim2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        if (delim1 == null || delim2 == null) {
            throw new IllegalArgumentException("指定分隔符不可为空");
        }
        boolean pass = false;
        StringTokenizer answertokenizer = new StringTokenizer(str1, delim1);
        StringTokenizer usranstokenizer = new StringTokenizer(str2, delim2);
        int anscount = answertokenizer.countTokens();
        if (anscount == usranstokenizer.countTokens()) {
            int i = 0;
            String[] answers = new String[anscount];
            while (answertokenizer.hasMoreTokens()) {
                answers[i++] = answertokenizer.nextToken();
            }
            String token = null;
            while (usranstokenizer.hasMoreTokens()) {
                pass = false;
                token = usranstokenizer.nextToken();
                for (i = 0; i < anscount; i++) {
                    if (token.equals(answers[i])) {
                        pass = true;
                        break;
                    }
                }
                if (pass == false)
                    break;
            }
        }
        return pass;
    }

    public static String[] getNonNullStringArray(String[] strs) {
        if (strs == null) {
            return null;
        }
        List strList = new ArrayList();
        for (String str : strs) {
            if (str == null || str.equals("")) {
                continue;
            }
            strList.add(str);
        }
        if (strList.size() == 0) {
            return null;
        } else {
            return (String[]) strList.toArray(new String[]{});
        }
    }

    /**
     * 判断字符串arg1是否在字符串数组arg0中<br>
     * 如果arg0或arg1为null，返回false
     *
     * @param arg0
     * @param arg1
     * @return
     */
    public static boolean inStringArray(String[] arg0, String arg1) {
        if (arg0 == null || arg0.length == 0 || arg1 == null) {
            return false;
        }
        for (String str : arg0) {
            if (arg1.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤HTML标签（StringEscapeUtils.escapeHtml），将<、>等转义
     *
     * @param html
     * @return
     */
    public static String escapeHTML(Number html) {
        if (html != null) {
            return html.toString();
        }
        return null;
    }

    /**
     * 过滤HTML标签，剔除<>、</>
     *
     * @param html
     * @return
     */
    public static String wipeHTML(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>|<[^>]*/>|</[^>]*>", "");
    }

    public static String getSetterName(String fieldName) {
        String first = fieldName.substring(0, 1);
        return "set" + first.toUpperCase() + fieldName.substring(1);
    }

    public static String getGetterName(String fieldName) {
        String first = fieldName.substring(0, 1);
        return "get" + first.toUpperCase() + fieldName.substring(1);
    }

    public static boolean isEmptyOrNull(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDate(String str) {
        if (isEmptyOrNull(str)) {
            return false;
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setLenient(true);
            try {
                df.parse(str);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
    }

    public static Date getDate(String str) {
        if (isEmptyOrNull(str)) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(true);
            try {
                return df.parse(str);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public static Date getNextDate(String str) {
        if (isEmptyOrNull(str)) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(true);
            try {
                df.parse(str);
                df.getCalendar().add(Calendar.DAY_OF_MONTH, 1);
                return df.getCalendar().getTime();
            } catch (ParseException e) {
                return null;
            }
        }
    }

    /**
     * 验证是否为email
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (isEmptyOrNull(email)) {
            return false;
        } else {
            email = email.trim();
        }
        String expression = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static List getNonNullStringList(List strs) {
        if (strs == null) {
            return null;
        }
        List strList = new ArrayList();
        for (int i = 0; i < strs.size(); i++) {
            String str = (String) strs.get(i);
            if (str == null || str.equals("")) {
                continue;
            }
            strList.add(str);
        }
        if (strList.size() == 0) {
            return null;
        } else {
            return strList;
        }
    }

    public static String escapeAttr(Object data) {
        if (data == null || data.toString() == "null")
            return "";
        // return StringEscapeUtils.escapeXml(data.toString());
        return data.toString().replace("&", "&#38;").replace("\"", "&#34;")
                .replace("'", "&#39;");
    }


    /**
     * 验证是否为负数(小于0,不包含0)
     *
     * @param str
     * @return
     */
    public static boolean isNegative(String str) {
        if (isNumber(str)) {
            double d = Double.parseDouble(str);
            if (d < 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 验证是否为非负整数(大于等于0)
     *
     * @param str
     * @return
     */
    public static boolean isPositiveLong(String str) {
        if (isLong(str)) {
            Long d = Long.parseLong(str);
            if (d >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 验证是否为正整数(大于0,不包含0)
     *
     * @param str
     * @return
     */
    public static boolean isLong(String str) {
        if (isEmptyOrNull(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 如果对象为null则返回空串，否则调用String.valueOf()方法
     *
     * @param obj
     * @return
     */
    public static String nvl(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    public static String replace(String str) {
        if (str == null || str.trim().equals("")) {
            return str;
        } else {
            if (str.startsWith("[")) {
                str = str.substring(1, str.length());
            }
            if (str.endsWith("]")) {
                str = str.substring(0, str.length() - 1);
            }
            if (str.startsWith(",")) {
                str = str.substring(1, str.length());
            }
            if (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
            return str;
        }
    }

    public static boolean isEmpty(String cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(String cs) {
        return !isEmpty(cs);
    }

    public static int getWordCount(String s) {//获得字符串的字节长度
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    /**
     * 判断字符串是否是链接<br>
     * 支持域名判断<br>
     * 支持IP判断<br>
     * 支持端口判断<br>
     * <p>
     * 参考：<a href="http://blog.csdn.net/weasleyqi/article/details/7912647">正则表达式匹配URL</a>
     *
     * @param url
     * @return 判断字符串是否是链接，是则返回 true，否则返回 false
     * @author zhuyang.sun
     */
    public static boolean isUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group().equals(url);
        }
        return false;
    }


    /**
     * 判断字符串是否是http、https链接，<br>
     * 支持域名判断<br>
     * 支持IP判断<br>
     * 支持端口判断<br>
     * <p>
     * 参考：<a href="http://blog.csdn.net/weasleyqi/article/details/7912647">正则表达式匹配URL</a>
     *
     * @param url
     * @return 判断字符串是否是链接，是则返回 true，否则返回 false
     * @author zhuyang.sun
     */
    public static boolean isHttpUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        Pattern pattern = Pattern.compile(HTTP_URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group().equals(url);
        }
        return false;
    }

    /**
     * Integer转化成boolean
     * value == null ，return false;
     * value != 0 ,return true;
     * value == 0, return false;
     *
     * @param value
     * @return
     */
    public static boolean toBoolean(final Integer value) {
        if (value == null) {
            return false;
        }
        return value.intValue() != 0;
    }

    /**
     * trim string
     * if value is null or empty after trim ,return defaultStr
     * eg: value=" ",defaultStr="1" return "1";
     * value ="1" ,defaultStr="2" return "1";
     *
     * @param value
     * @param defaultStr
     * @return
     */
    public static String trimString(String value, String defaultStr) {
        String trimString = trimString(value);
        if (isEmptyOrNull(trimString)) {
            return defaultStr;
        }
        return trimString;
    }

    /**
     * 是否是在指定小数位数内的标准小数
     * 如
     * decimalPlaces=1，decimalStr=0.1时返回true
     * decimalPlaces=1，decimalStr=1 时返回true
     * decimalPlaces=1，decimalStr=0.01时返回false
     * decimalPlaces=1，decimalStr=0.时返回false
     * decimalPlaces=1，decimalStr=0.1d时返回false
     *
     * @param decimalStr    小数字符串
     * @param decimalPlaces 指定小数位数,要求大于0的正整数
     * @return
     */
    public static boolean isStandardDecimal(String decimalStr, int decimalPlaces) {
        if (isEmptyOrNull(decimalStr)) {
            return Boolean.FALSE.booleanValue();
        }
        Pattern pattern = Pattern.compile(String.format(NUMBER_DECIMAL_REGEX_NEED_FORMAT, decimalPlaces));
        Matcher matcher = pattern.matcher(decimalStr);

        return matcher.matches();
    }


    /**
     * <p>
     * 扩大字符串长度，从左边补充空格
     * </p>
     * <p/>
     * <pre>
     * StringUtil.leftPad(null, *)   = null
     * StringUtil.leftPad("", 3)     = "   "
     * StringUtil.leftPad("bat", 3)  = "bat"
     * StringUtil.leftPad("bat", 5)  = "  bat"
     * StringUtil.leftPad("bat", 1)  = "bat"
     * StringUtil.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  源字符串
     * @param size 扩大后的长度
     * @return String
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>
     * 扩大字符串长度，从左边补充空格
     * </p>
     * <p/>
     * <pre>
     * StringUtil.leftPad(null, *)   = null
     * StringUtil.leftPad("", 3)     = "   "
     * StringUtil.leftPad("bat", 3)  = "bat"
     * StringUtil.leftPad("bat", 5)  = "  bat"
     * StringUtil.leftPad("bat", 1)  = "bat"
     * StringUtil.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param number 源字符串
     * @param size   扩大后的长度
     * @return String
     */
    public static String leftPad(long number, int size, char padChar) {
        return leftPad(String.valueOf(number), size, padChar);
    }

    /**
     * <p>
     * 扩大字符串长度，从左边补充指定的字符
     * </p>
     * <p/>
     * <pre>
     * StringUtil.leftPad(null, *, *)     = null
     * StringUtil.leftPad("", 3, 'z')     = "zzz"
     * StringUtil.leftPad("bat", 3, 'z')  = "bat"
     * StringUtil.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtil.leftPad("bat", 1, 'z')  = "bat"
     * StringUtil.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     源字符串
     * @param size    扩大后的长度
     * @param padChar 补充的字符
     * @return String
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * <p>
     * 扩大字符串长度，从左边补充指定的字符
     * </p>
     * <p/>
     * <pre>
     * StringUtil.leftPad(null, *, *)      = null
     * StringUtil.leftPad("", 3, "z")      = "zzz"
     * StringUtil.leftPad("bat", 3, "yz")  = "bat"
     * StringUtil.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtil.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtil.leftPad("bat", 1, "yz")  = "bat"
     * StringUtil.leftPad("bat", -1, "yz") = "bat"
     * StringUtil.leftPad("bat", 5, null)  = "  bat"
     * StringUtil.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    源字符串
     * @param size   扩大后的长度
     * @param padStr 补充的字符串
     * @return String
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * <p>
     * 将某个字符重复N次.
     * </p>
     *
     * @param ch     某个字符
     * @param repeat 重复次数
     * @return String
     */
    public static String repeat(char ch, int repeat) {
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String aroundQuotes(String str) {
        return str != null && !str.isEmpty() ? "\"" + doubleQuotes(str) + "\"" : "";
    }

    private static String doubleQuotes(String str) {
        String tempDescription = str;
        if (str.contains("\"")) {
            tempDescription = str.replaceAll("\"", "\"\"");
        }

        return tempDescription;
    }

    public static boolean isBoolean(String str) {
        if(str.equalsIgnoreCase("true")||str.equalsIgnoreCase("false")){
            return true;
        }
        return false;
    }

}
