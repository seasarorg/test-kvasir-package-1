package org.seasar.kvasir.util.collection;



/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PropertiesUtils
{
    private static final String[]   PADDINGS = new String[]{
        null, "000", "00", "0", "" };


    private PropertiesUtils()
    {
    }


    public static String escape(String str)
    {
        return escape(str, true);
    }


    public static String escape(String str, boolean unicodeEscape)
    {
        int n = str.length();
        StringBuffer sb = new StringBuffer(n * 3 + 1);
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                sb.append("\\\\");
            } else if (ch == '\t') {
                sb.append("\\t");
            } else if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\r') {
                sb.append("\\r");
            } else if (ch < 0x20 || ch > 0x7e) {
                if (!unicodeEscape && ch > 0xff) {
                    sb.append(ch);
                } else {
                    sb.append("\\u");
                    String hex = Integer.toHexString(ch);
                    sb.append(PADDINGS[hex.length()]);
                    sb.append(hex);
                }
            } else if (ch == '#' || ch == '!' || ch == '=' || ch == ':') {
                sb.append("\\");
                sb.append(ch);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }


    public static String unescape(String str, String delims)
    {
        int n = str.length();
        StringBuffer sb = new StringBuffer(n + 1);
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                if (i + 1 < n) {
                    ch = str.charAt(++i);
                    if (ch == 't') {
                        sb.append('\t');
                    } else if (ch == 'n') {
                        sb.append('\n');
                    } else if (ch == 'r') {
                        sb.append('\r');
                    } else if (ch == 'u') {
                        sb.append((char)Integer.parseInt(
                            str.substring(i + 1, i + 5), 16));
                        i += 4;
                    } else {
                        sb.append(ch);
                    }
                }
            } else if (delims.indexOf(ch) >= 0) {
                break;
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }
}
