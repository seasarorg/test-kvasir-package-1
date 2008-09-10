package org.seasar.kvasir.base.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class SQLUtils
{
    private SQLUtils()
    {
    }


    public static PreparedStatement setParameters(PreparedStatement pst,
        Object[] params, int offset)
        throws SQLException
    {
        if (params != null) {
            int idx = offset;
            for (int i = 0; i < params.length; i++) {
                pst.setObject(idx++, params[i]);
            }
        }
        return pst;
    }


    public static PreparedStatement setParameters(PreparedStatement pst,
        Collection<Object> params, int offset)
        throws SQLException
    {
        if (params != null) {
            int idx = offset;
            for (Iterator<Object> itr = params.iterator(); itr.hasNext();) {
                pst.setObject(idx++, itr.next());
            }
        }
        return pst;
    }


    /**
     * 指定された文字列中の指定された文字を指定された文字でエスケープします。
     *
     * @param literal 文字列。
     * @param chars エスケープする文字の列。
     * @param escape エスケープするための文字。
     * この文字自体もエスケープされます。
     * @return エスケープされた文字列。
     */
    public static String escape(String literal, String chars, char escape)
    {
        if (literal == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(literal, chars
            + String.valueOf(escape), true);
        StringBuffer result = new StringBuffer();
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (tkn.length() == 1) {
                char ch = tkn.charAt(0);
                if (ch == escape) {
                    result.append(escape);
                } else {
                    int n = chars.length();
                    for (int i = 0; i < n; i++) {
                        if (ch == chars.charAt(i)) {
                            result.append(escape);
                            break;
                        }
                    }
                }
            }
            result.append(tkn);
        }

        return result.toString();
    }
}
