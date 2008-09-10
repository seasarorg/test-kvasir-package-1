package org.seasar.kvasir.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.seasar.kvasir.util.io.IORuntimeException;


/**
 * @author YOKOTA Takehiko
 */
public class StringUtils
{
    private static final String SP = System.getProperty("line.separator");


    private StringUtils()
    {
    }


    public static String trimLeft(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(i);
            }
        }
        return "";
    }


    public static String trimRight(String str)
    {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(0, i + 1);
            }
        }
        return "";
    }


    public static String normalizeLineSeparator(String string)
    {
        if (string == null) {
            return null;
        }

        BufferedReader br = new BufferedReader(new StringReader(string));
        StringBuffer sb = new StringBuffer(string.length() * 2);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(SP);
            }
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        if (string.length() == 0 || string.endsWith("\n")
            || string.endsWith("\r\n")) {
            return sb.toString();
        } else {
            return sb.substring(0, sb.length() - SP.length());
        }
    }


    /**
     * 指定された文字列を指定された文字でクオートします。
     * <p>指定された文字列の両側を指定されたクオート文字で囲います。
     * 文字列中にクオート文字を含む場合は\記号でエスケープします。</p>
     *
     * @param str 文字列。
     * @param quote クオート文字。
     * @return クオート下文字列。
     */
    public static String quoteString(String str, char quote)
    {
        if (str.indexOf(quote) < 0) {
            return quote + str + quote;
        } else {
            String quoteString = String.valueOf(quote);
            StringBuffer sb = new StringBuffer(quoteString);
            StringTokenizer st = new StringTokenizer(str, quoteString, true);
            while (st.hasMoreTokens()) {
                String tkn = st.nextToken();
                if (tkn.equals(quoteString)) {
                    sb.append("\\");
                }
                sb.append(tkn);
            }
            sb.append(quote);
            return sb.toString();
        }
    }


    public static String replace(String str, String from, String to)
    {
        if (str == null || from == null) {
            return str;
        }
        if (to == null) {
            to = "";
        }

        StringBuffer sb = new StringBuffer();
        int pre = 0;
        int idx;
        while ((idx = str.indexOf(from, pre)) >= 0) {
            sb.append(str.substring(pre, idx)).append(to);
            pre = idx + from.length();
        }
        sb.append(str.substring(pre));
        return sb.toString();
    }
}
