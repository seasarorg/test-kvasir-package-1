package org.seasar.kvasir.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * 文字列を数値などの他のクラスのインスタンスに変換したり、
 * その逆を行なったりするためのユーティリティクラスです。
 *
 * @author YOKOTA Takehiko
 */
public class PropertyUtils
{
    /*
     * constructors
     */

    private PropertyUtils()
    {
    }


    /*
     * static scope methods
     */

    public static boolean valueOf(String str, boolean defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(str) || "on".equalsIgnoreCase(str)
            || "yes".equalsIgnoreCase(str);
    }


    public static boolean valueOf(Boolean obj, boolean defaultValue)
    {
        if (obj == null) {
            return defaultValue;
        }
        return obj.booleanValue();
    }


    public static boolean valueOf(Object obj, boolean defaultValue)
    {
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof Boolean) {
            return ((Boolean)obj).booleanValue();
        } else if (obj instanceof Number) {
            return (((Number)obj).intValue() != 0);
        } else {
            String str = obj.toString();
            return ("true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str));
        }

    }


    public static Boolean valueOf(String str, Boolean defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        return Boolean.valueOf("true".equalsIgnoreCase(str)
            || "on".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str));
    }


    public static byte valueOf(String str, byte defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        byte value = defaultValue;
        try {
            value = Byte.parseByte(str);
        } catch (Throwable t) {
            try {
                value = (byte)Double.parseDouble(str);
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static Byte valueOf(String str, Byte defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Byte value = defaultValue;
        try {
            value = Byte.valueOf(str);
        } catch (Throwable t) {
            try {
                value = new Byte((byte)Double.parseDouble(str));
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static short valueOf(String str, short defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        short value = defaultValue;
        try {
            value = Short.parseShort(str);
        } catch (Throwable t) {
            try {
                value = (short)Double.parseDouble(str);
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static Short valueOf(String str, Short defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Short value = defaultValue;
        try {
            value = Short.valueOf(str);
        } catch (Throwable t) {
            try {
                value = new Short((short)Double.parseDouble(str));
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static int valueOf(String str, int defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        int value = defaultValue;
        try {
            value = Integer.parseInt(str);
        } catch (Throwable t) {
            try {
                value = (int)Double.parseDouble(str);
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static Integer valueOf(String str, Integer defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Integer value = defaultValue;
        try {
            value = Integer.valueOf(str);
        } catch (Throwable t) {
            try {
                value = new Integer((int)Double.parseDouble(str));
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static long valueOf(String str, long defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        long value = defaultValue;
        try {
            value = Long.parseLong(str);
        } catch (Throwable t) {
            try {
                value = (long)Double.parseDouble(str);
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static Long valueOf(String str, Long defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Long value = defaultValue;
        try {
            value = Long.valueOf(str);
        } catch (Throwable t) {
            try {
                value = new Long((long)Double.parseDouble(str));
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static float valueOf(String str, float defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        float value = defaultValue;
        try {
            value = Float.parseFloat(str);
        } catch (Throwable t) {
            try {
                value = (float)Double.parseDouble(str);
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static Float valueOf(String str, Float defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Float value = defaultValue;
        try {
            value = Float.valueOf(str);
        } catch (Throwable t) {
            try {
                value = new Float((float)Double.parseDouble(str));
            } catch (Throwable t2) {
                ;
            }
        }
        return value;
    }


    public static double valueOf(String str, double defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        double value = defaultValue;
        try {
            value = Double.parseDouble(str);
        } catch (Throwable t) {
            ;
        }
        return value;
    }


    public static Double valueOf(String str, Double defaultValue)
    {
        if (str == null) {
            return defaultValue;
        }
        Double value = defaultValue;
        try {
            value = Double.valueOf(str);
        } catch (Throwable t) {
            ;
        }
        return value;
    }


    /**
     * <code>str</code>自身を返しますが、
     * <code>str</code>がnullである場合は<code>defaultValue</code>
     * を返します。
     *
     * @param str 値。
     * @param defaultValue <code>str</code>がnullの時に返す値。
     * @return 値。
     */
    public static String valueOf(String str, String defaultValue)
    {
        if (str == null) {
            return defaultValue;
        } else {
            return str;
        }
    }


    public static String valueOf(Object obj, String defaultValue)
    {
        if (obj == null) {
            return defaultValue;
        } else if (obj.getClass().isArray()) {
            if (Array.getLength(obj) == 0) {
                return defaultValue;
            }
            Object value = Array.get(obj, 0);
            if (value == null) {
                return defaultValue;
            } else {
                return value.toString();
            }
        } else {
            return obj.toString();
        }
    }


    /**
     * <code>obj</code>自身を返しますが、
     * <code>obj</code>がnullである場合は<code>defaultValue</code>
     * を返します。
     *
     * @param obj 値。
     * @param defaultValue <code>obj</code>がnullの時に返す値。
     * @return 値。
     */
    public static Object valueOf(String obj, Object defaultValue)
    {
        if (obj == null) {
            return defaultValue;
        } else {
            return obj;
        }
    }


    public static String toStringOrNull(Object obj)
    {
        if (obj == null) {
            return null;
        } else {
            return obj.toString();
        }
    }


    public static String[] toLines(String str)
    {
        return toLines(str, ",\r\n");
    }


    public static String[] toLines(String str, String delims)
    {
        if (str == null) {
            return new String[0];
        }
        str = str.trim();
        if (str.length() == 0) {
            return new String[0];
        }

        List list = new ArrayList();
        StringTokenizer st = new StringTokenizer(str, delims);
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken().trim();
            if (tkn.length() > 0) {
                list.add(tkn);
            }
        }

        return (String[])list.toArray(new String[0]);
    }


    public static String toString(String[] lines)
    {
        if (lines == null || lines.length == 0) {
            return "";
        }

        String separator = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]);
            sb.append(separator);
        }

        return sb.toString();
    }


    public static String join(String[] strs)
    {
        if (strs == null || strs.length == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(strs[i]);
        }

        return sb.toString();
    }


    public static String[] toArray(String str)
    {
        return toArray(str, "\r\n", false);
    }


    public static String[] toArray(String str, String delims, boolean trim)
    {
        if (str == null) {
            return new String[0];
        }

        Set set = new HashSet();
        for (int i = 0; i < delims.length(); i++) {
            set.add(String.valueOf(delims.charAt(i)));
        }

        List list = new ArrayList();
        if (set.contains("\r") && set.contains("\n")) {
            StringReader sr = new StringReader(str);
            BufferedReader in = new BufferedReader(sr);
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    split(line, delims, set, trim, list);
                }
            } catch (IOException ex) {
                // Can't happen!
                throw new RuntimeException(ex);
            }
        } else {
            split(str, delims, set, trim, list);
        }
        return (String[])list.toArray(new String[0]);
    }


    /*
     * private scope methods
     */

    private static void split(String line, String delims, Set set,
        boolean trim, List list)
    {
        if (line.length() == 0) {
            list.add(line);
        }

        StringTokenizer st = new StringTokenizer(line, delims, true);
        boolean delim = false;
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (!set.contains(tkn)) {
                if (trim) {
                    tkn = tkn.trim();
                }
                list.add(tkn);
                delim = false;
            } else {
                if (delim) {
                    list.add("");
                }
                delim = true;
            }
        }
        if (delim) {
            list.add("");
        }
    }
}
