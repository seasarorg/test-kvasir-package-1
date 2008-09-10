package org.seasar.kvasir.page.dao;

import java.util.ArrayList;
import java.util.List;


public class WhereTokenizer
{
    public static final int         TYPE_SPACE      = 0;
    public static final int         TYPE_STRING     = 1;
    public static final int         TYPE_NUMBER     = 2;
    public static final int         TYPE_SYMBOL     = 3;
    public static final int         TYPE_PARAM      = 4;
    public static final int         TYPE_OTHER      = 5;

    private static final String DELIMS = "()<>=!+-*/,";


    private String      str_;
    private int         len_;
    private Token[]     tokens_;
    private int         idx_;


    /*
     * constructors
     */

    public WhereTokenizer(String str)
    {
        str_ = str;
        len_ = str_.length();

        List<Token> list = new ArrayList<Token>();
        parse(list);

        tokens_ = list.toArray(new Token[0]);
        idx_ = 0;
    }


    /*
     * public scope methods
     */

    public boolean hasMoreTokens()
    {
        return (idx_ < tokens_.length);
    }


    public Token nextToken()
    {
        if (!hasMoreTokens()) {
            return null;
        }

        return tokens_[idx_++];
    }


    /*
     * private scope methods
     */

    private void parse(List<Token> list)
    {
        int stat = 0;
        int pos = 0;
        int i = 0;
        StringBuffer sb = null;
        boolean dot = false;
        for (; i < len_; i++) {
            char ch = str_.charAt(i);
            char ch2 = (i + 1 < len_ ? str_.charAt(i + 1) : (char)0);
            char ch3 = (i + 2 < len_ ? str_.charAt(i + 2) : (char)0);
            if (stat == 0) {
                if (ch == ';') {
                    throw new IllegalArgumentException(
                        "Invalid char: '" + ch + "' in string: " + str_);
                } else if (ch == ' ') {
                    ;
                } else {
                    if (pos < i) {
                        list.add(new Token(TYPE_SPACE, str_.substring(pos, i)));
                        pos = i;
                    }

                    if (ch == '?') {
                        list.add(new Token(TYPE_PARAM, String.valueOf(ch)));
                        pos++;
                    } else if ((((ch == '+') || (ch == '-'))
                    && (((ch2 == '.') && (ch3 >= '0') && (ch3 <= '9'))
                    || ((ch2 >= '0') && (ch2 <= '9'))))
                    || ((ch == '.') && (ch2 >= '0') && (ch2 <= '9'))
                    || ((ch >= '0') && (ch <= '9'))) {
                        dot = ((((ch == '+') || (ch == '-')) && (ch2 == '.'))
                            || (ch == '.'));
                        stat = 2;
                    } else if (ch == '.') {
                        throw new IllegalArgumentException(
                            "Invalid char: '" + ch + "' in string: " + str_);
                    } else if (DELIMS.indexOf(ch) >= 0) {
                        stat = 4;
                    } else if (ch == '\'') {
                        pos++;
                        stat = 1;
                    } else if ((ch == '_') || ((ch >= 'a') && (ch <= 'z'))
                    || ((ch >= 'A') && (ch <= 'Z'))) {
                        stat = 3;
                    } else {
                        list.add(new Token(TYPE_OTHER, String.valueOf(ch)));
                        pos++;
                    }
                }
            } else if (stat == 1) {
                // 文字列。
                if ((ch == '\\') || ((ch == '\'') && (ch2 == '\''))) {
                    if (sb == null) {
                        sb = new StringBuffer(len_);
                    }
                    sb.append(str_.substring(pos, i));
                    i++;
                    pos = i;
                } else if (ch == '\'') {
                    String value = str_.substring(pos, i);
                    if (sb != null) {
                        sb.append(value);
                        value = sb.toString();
                        sb = null;
                    }
                    list.add(new Token(TYPE_STRING, value));
                    pos = i + 1;
                    stat = 0;
                } else {
                    ;
                }
            } else if (stat == 2) {
                // 数値。
                if (((ch >= '0') && (ch <= '9')) || (!dot && (ch == '.'))) {
                    if (!dot && (ch == '.')) {
                        dot = true;
                    }
                } else {
                    list.add(new Token(TYPE_NUMBER, str_.substring(pos, i)));
                    pos = i;
                    i--;
                    stat = 0;
                }
            } else if (stat == 3) {
                // シンボル。
                if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))
                || ((ch >= '0') && (ch <= '9')) || (ch == '_') || (ch == '.')
                || (ch == '[') || (ch == ']')) {
                    ;
                } else {
                    list.add(new Token(TYPE_SYMBOL, str_.substring(pos, i)));
                    pos = i;
                    i--;
                    stat = 0;
                }
            } else if (stat == 4) {
                // 区切り記号。
                if (DELIMS.indexOf(ch) >= 0) {
                    ;
                } else {
                    list.add(new Token(TYPE_OTHER, str_.substring(pos, i)));
                    pos = i;
                    i--;
                    stat = 0;
                }
            } else {
                throw new RuntimeException("LOGIC ERROR");
            }
        }
        if (pos < i) {
            int type;
            if (stat == 0) {
                type = TYPE_SPACE;
            } else if (stat == 1) {
                throw new IllegalArgumentException(
                    "Run away string: " + str_);
            } else if (stat == 2) {
                type = TYPE_NUMBER;
            } else if (stat == 3) {
                type = TYPE_SYMBOL;
            } else if (stat == 4) {
                type = TYPE_OTHER;
            } else {
                throw new RuntimeException("LOGIC ERROR");
            }
            list.add(new Token(type, str_.substring(pos, i)));
        }

        tokens_ = list.toArray(new Token[0]);
    }


    /*
     * inner classes
     */

    public static class Token
    {
        private int     type_;
        private String  value_;

        public Token(int type, String value)
        {
            type_ = type;
            value_ = value;
        }

        public int getType()
        {
            return type_;
        }

        public String getValue()
        {
            return value_;
        }
    }
}
