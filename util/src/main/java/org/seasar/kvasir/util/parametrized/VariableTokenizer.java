package org.seasar.kvasir.util.parametrized;

import java.util.ArrayList;
import java.util.List;


public class VariableTokenizer
{
    public static final int         TYPE_CONSTANT   = 0;
    public static final int         TYPE_VARIABLE   = 1;
    public static final int         TYPE_EXPRESSION = 2;


    private String      str_;
    private int         len_;
    private boolean     recognizeExpression_;
    private Token[]     tokens_;
    private int         idx_;


    /*
     * constructors
     */

    public VariableTokenizer(String str)
    {
        this(str, false);
    }


    public VariableTokenizer(String str, boolean recognizeExpression)
    {
        str_ = str;
        len_ = str_.length();
        recognizeExpression_ = recognizeExpression;

        List list = new ArrayList();
        parse(0, list, false, (char)0);

        tokens_ = (Token[])list.toArray(new Token[0]);
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

    private int parse(int pos, List list, boolean nested, char end)
    {
        // 高速化のため。nestedでない時しか検査しないのは例えば
        // a${a}b${b}......z${z}
        // のような文字列を解釈した場合には却って効率が悪い気がするから。
        if (!nested && str_.indexOf("${", pos) < 0
        && (recognizeExpression_ && str_.indexOf("$[", pos) < 0)
        && str_.indexOf("\\", pos) < 0) {
            if (list != null) {
                list.add(new Token(TYPE_CONSTANT, str_.substring(pos)));
            }
            return len_;
        }

        StringBuffer sb = null;
        int pre = pos;
        for (int i = pos; i < len_; i++) {
            char ch = str_.charAt(i);
            char ch2 = (i + 1 < len_ ? str_.charAt(i + 1) : (char)0);
            if (ch == '\\') {
                if (pre < i) {
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    sb.append(str_.substring(pre, i));
                }
                i++;
                if (i < len_) {
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    sb.append(ch2);
                }
                pre = i + 1;
            } else if (ch == '$'
            && (ch2 == '{' || (recognizeExpression_ && ch2 == '['))) {
                String chunk = null;
                if (pre < i) {
                    chunk = str_.substring(pre, i);
                    if (sb != null) {
                        sb.append(chunk);
                        chunk = sb.toString();
                        sb.delete(0, sb.length());
                    }
                } else {
                    if (sb != null && sb.length() > 0) {
                        chunk = sb.toString();
                        sb.delete(0, sb.length());
                    }
                }
                pre = i + 2;
                if (chunk != null) {
                    list.add(new Token(TYPE_CONSTANT, chunk));
                }
                int type;
                char ech;
                if (ch2 == '{') {
                    type = TYPE_VARIABLE;
                    ech = '}';
                } else if (ch2 == '[') {
                    type = TYPE_EXPRESSION;
                    ech = ']';
                } else {
                    throw new RuntimeException("Logic error: " + ch2);
                }
                i = findEdge(str_, pre, ech);
                list.add(new Token(type, str_.substring(pre - 2, i + 1)));
                pre = i + 1;
            } else if (nested  && ch == end) {
                return i;
            }
        }
        if (sb == null && pre == 0) {
            list.add(new Token(TYPE_CONSTANT, str_.substring(pos)));
        } else {
            String chunk = null;
            if (pre < len_) {
                chunk = str_.substring(pre);
                if (sb != null && sb.length() > 0) {
                    sb.append(chunk);
                    chunk = sb.toString();
                }
            } else {
                if (sb != null && sb.length() > 0) {
                    chunk = sb.toString();
                }
            }
            if (chunk != null) {
                list.add(new Token(TYPE_CONSTANT, chunk));
            }
        }

        return len_;
    }


    /**
     * 指定された終了文字が見つかるまで指定された文字列を走査します。
     * <p>文字列中、<code>${...}</code>と<code>$[...]</code>はスキップします。
     * また<code>\</code>でエスケープされている文字は無条件でスキップします。
     * </p>
     *
     * @param str 走査する文字列。
     * @param pos 走査の開始位置。
     * @param end 終了文字。
     * @param 終了文字を検出した位置。
     * 終了文字が見つからなかった場合は文字列の長さ-1を返します。
     */
    private static int findEdge(String str, int pos, char end)
    {
        int n = str.length();
        for (int i = pos; i < n; i++) {
            char ch = str.charAt(i);
            char ch2 = (i + 1 < n ? str.charAt(i + 1) : (char)0);
            if (ch == '\\') {
                i++;
            } else if (ch == '$' && (ch2 == '{' || ch2 == '[')) {
                i = findEdge(str, i + 2, (ch2 == '{' ? '}' : ']'));
            } else if (ch == end) {
                return i;
            }
        }

        return n - 1;
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
