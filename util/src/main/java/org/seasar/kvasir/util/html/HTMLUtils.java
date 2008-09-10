package org.seasar.kvasir.util.html;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


public class HTMLUtils
{
    private static final char[] byteTable_ = new char[] { '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static final int PREFETCH_BUFSIZE = 1024;

    private static final String PREFIX_META = "<meta ";

    private static final String PATTERN_HTTPEQUIV = " http-equiv=";

    private static final Pattern[] META_PATTERNS = new Pattern[] {
        Pattern
            .compile(
                "<meta\\s+http-equiv=(['\"])content-type\\1\\s+content=(['\"])[^;]*;\\s*charset=(.*?)\\2",
                Pattern.CASE_INSENSITIVE),
        Pattern
            .compile(
                "<meta\\s+http-equiv=content-type\\s+content=[^;]*;charse(t)(=)(.*?)[\\s|>|/]",
                Pattern.CASE_INSENSITIVE) };

    private static final String PREFIX_CLASS = "class=";


    protected HTMLUtils()
    {
    }


    /*
     * static methods
     */

    public static String filterLines(String text)
    {
        return filterLines(text, false);
    }


    public static String filterLines(String text, boolean toNbsp)
    {
        if (text == null) {
            return null;
        }

        boolean endsWithSeparator = false;
        int n = text.length();
        if (n > 0) {
            char ch = text.charAt(n - 1);
            if (ch == '\r' || ch == '\n') {
                endsWithSeparator = true;
            }
        }

        String separator = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        String delim = "";

        StringReader sr = new StringReader(text);
        BufferedReader in = new BufferedReader(sr);
        try {
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(delim);
                if (toNbsp) {
                    delim = "<br />" + separator;
                } else {
                    delim = separator;
                }
                sb.append(filter(line, toNbsp));
            }
        } catch (IOException ex) {
            // Can't happen!
            throw new IORuntimeException(ex);
        }
        if (endsWithSeparator) {
            sb.append(delim);
        }

        return sb.toString();
    }


    public static String filter(String line)
    {
        return filter(line, false);
    }


    public static String filter(String line, boolean toNbsp)
    {
        if (line == null) {
            return line;
        }

        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(line, "&<> \"'", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String encode;
            if (token.equals("&")) {
                encode = "&amp;";
            } else if (token.equals("<")) {
                encode = "&lt;";
            } else if (token.equals(">")) {
                encode = "&gt;";
            } else if (token.equals(" ")) {
                if (toNbsp) {
                    encode = "&nbsp;";
                } else {
                    encode = " ";
                }
            } else if (token.equals("\"")) {
                encode = "&quot;";
            } else if (token.equals("'")) {
                encode = "&#39;";
            } else {
                encode = token;
            }
            sb.append(encode);
        }

        return sb.toString();
    }


    public static String filter(String str, int unit, String delimiter)
    {
        if (str == null) {
            return null;
        }

        int n = str.length();
        int pre = 0;
        int count = 0;
        StringBuffer sb = null;
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch > 0x20 && ch < 0x80) {
                count++;
                if (count >= unit) {
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    sb.append(filter(str.substring(pre, i + 1)));
                    sb.append(delimiter);
                    count = 0;
                    pre = i + 1;
                }
            } else {
                count = 0;
            }
        }
        if (sb == null) {
            return filter(str);
        } else {
            sb.append(filter(str.substring(pre)));
            return sb.toString();
        }
    }


    public static String defilterLines(String html)
    {
        if (html == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        String separator = System.getProperty("line.separator");

        HTMLTokenizer ht = new HTMLTokenizer(html);
        while (ht.hasMoreTokens()) {
            HTMLTokenizer.Token tkn = ht.nextToken();
            int type = tkn.getType();
            String tag = tkn.getTagName();
            if (tag != null) {
                tag = tag.toLowerCase();
            }

            if (type == HTMLTokenizer.BODY) {
                defilter(sb, tkn.toString());
            } else if (type == HTMLTokenizer.TAG) {
                if ("br".equals(tag)) {
                    sb.append(separator);
                }
            } else if (type == HTMLTokenizer.COMMENT) {
                ;
            } else {
                throw new RuntimeException("Unknown type: " + tkn.toString());
            }
        }

        return sb.toString();
    }


    public static String defilter(String html)
    {
        if (html == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        defilter(sb, html);
        return sb.toString();
    }


    public static void defilter(StringBuffer sb, String html)
    {
        String htmlLowerCase = html.toLowerCase();

        int pre = 0;
        int idx;
        while ((idx = html.indexOf("&", pre)) >= 0) {
            sb.append(html.substring(pre, idx));

            int semi = html.indexOf(";", idx + 1);
            if (semi < 0) {
                pre = idx;
                break;
            }

            String tkn = htmlLowerCase.substring(idx + 1, semi);
            String decode;
            if (tkn.equals("amp")) {
                decode = "&";
            } else if (tkn.equals("lt")) {
                decode = "<";
            } else if (tkn.equals("gt")) {
                decode = ">";
            } else if (tkn.equals("nbsp")) {
                decode = " ";
            } else if (tkn.equals("quot")) {
                decode = "\"";
            } else if (tkn.equals("apos")) {
                decode = "'";
            } else if (tkn.startsWith("#")) {
                String code = tkn.substring(1);
                int radix = 10;
                if (code.startsWith("x")) {
                    code = code.substring(1);
                    radix = 16;
                }
                try {
                    decode = String
                        .valueOf((char)Integer.parseInt(code, radix));
                } catch (Throwable t) {
                    decode = html.substring(idx, semi + 1);
                }
            } else {
                decode = html.substring(idx, semi + 1);
            }

            sb.append(decode);
            pre = semi + 1;
        }
        sb.append(html.substring(pre));

        return;
    }


    public static String stripHTML(String html)
    {
        if (html == null) {
            return null;
        }

        // ヘッダ等があれば取り除く。
        StringBuffer sb = new StringBuffer();
        String ignoreUntil = null;
        int pre = 0;
        int idx;
        while ((idx = html.indexOf("<", pre)) >= 0) {
            if (ignoreUntil == null) {
                sb.append(html.substring(pre, idx));
            }

            int end = html.indexOf(">", idx + 1);
            if (end < 0) {
                pre = idx;
                break;
            }

            String tag = html.substring(idx, end + 1);
            String itag = tag.toLowerCase();
            if (ignoreUntil == null) {
                if (itag.equals("<html>") || itag.equals("</html>")
                    || itag.equals("<body>") || itag.equals("</body>")) {
                    ;
                } else if (itag.equals("<head>")) {
                    // 無視期間を開始する。
                    ignoreUntil = "</head>";
                } else {
                    sb.append(tag);
                }
            } else if (itag.equals(ignoreUntil)) {
                // 無視期間を終了する。
                ignoreUntil = null;
            } else {
                // 無視する。
                ;
            }

            pre = end + 1;
        }

        if (ignoreUntil == null) {
            sb.append(html.substring(pre));
        }

        return sb.toString();
    }


    public static String formatHTML(String text)
    {
        if (text.length() < 6
            || !text.substring(0, 6).equalsIgnoreCase("<html>")) {
            String separator = System.getProperty("line.separator");
            StringBuffer sb = new StringBuffer();
            sb.append("<html>");
            sb.append(separator);
            sb.append("<body>");
            sb.append(separator);
            sb.append(text);
            sb.append("</body>");
            sb.append(separator);
            sb.append("</html>");
            sb.append(separator);

            text = sb.toString();
        }

        return text;
    }


    public static String ignoreTags(String html)
    {
        if (html == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        boolean ignore = false;
        StringTokenizer st = new StringTokenizer(html, "<>", true);
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (tkn.equals("<")) {
                ignore = true;
            } else if (tkn.equals(">")) {
                ignore = false;
                sb.append(" ");
            } else if (!ignore) {
                sb.append(tkn);
            }
        }

        return sb.toString();
    }


    public static String encodeURL(String url, String encoding)
    {
        return encodeURL(url, encoding, false);
    }


    public static String encodeURL(String url, String encoding, boolean forURL)
    {
        StringBuffer sb = new StringBuffer();
        byte[] bytes;
        try {
            bytes = url.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        for (int i = 0; i < bytes.length; i++) {
            int ch;
            if (bytes[i] >= 0) {
                ch = bytes[i];
            } else {
                ch = bytes[i] + 256;
            }
            if (ch >= 0x30 && ch <= 0x39 || ch >= 0x41 && ch <= 0x5a
                || ch >= 0x61 && ch <= 0x7a || ch == 0x2a || ch == 0x2d
                || ch == 0x2e || ch == 0x5f) {
                sb.append((char)ch);
            } else if (!forURL && ch == 0x20) {
                sb.append('+');
            } else if (forURL && ch < 128 && ch != 0x20) {
                sb.append((char)ch);
            } else {
                sb.append('%');
                String hex = Integer.toHexString(ch);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
        }

        return sb.toString();
    }


    public static String decodeURL(String url, String encoding)
    {
        if (url == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int n = url.length();
        for (int i = 0; i < n; i++) {
            char ch = url.charAt(i);
            if (ch == '+') {
                out.write(0x20);
            } else if (ch == '%') {
                if (i < n - 2) {
                    char hi = url.charAt(++i);
                    char lo = url.charAt(++i);
                    int chr = Character.digit(hi, 16) * 16
                        + Character.digit(lo, 16);
                    out.write(chr);
                } else {
                    break;
                }
            } else {
                out.write(ch);
            }
        }

        try {
            return out.toString(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static String readHTML(InputStream in)
    {
        return readHTML(in, "ISO-8859-1", true);
    }


    /**
     * 指定されたHTMLの入力ストリームから文字列を構築します。
     * <p><code>preferEmbeddedCharset</code>が<code>true</code>の場合は、
     * 入力ストリーム中にmetaタグで指定されている文字コードが<code>charset</code>で指定した文字コードに優先します。
     * </p>
     * <p><code>preferEmbeddedCharset</code>が<code>false</code>の場合は、
     * <code>charset</code>で指定した文字コードが入力ストリーム中にmetaタグで指定されている文字コードに優先します。
     * </p>
     * <p>文字コードがmetaタグで指定されていない場合でかつ<code>charset</code>としてnullを指定した場合は、
     * 文字コードはISO-8859-1とみなします。
     * </p>
     *
     * @param in HTMLの入力ストリーム。nullを指定した場合はnullを返します。
     * @param charset 文字コード。
     * @param preferEmbeddedCharset metaタグで指定されている文字コードを優先させるかどうか。
     * @return HTML文字列。
     */
    public static String readHTML(InputStream in, String charset,
        boolean preferEmbeddedCharset)
    {
        if (in == null) {
            return null;
        }
        BufferedInputStream bis = null;
        try {
            if (in instanceof BufferedInputStream) {
                bis = (BufferedInputStream)in;
            } else {
                bis = new BufferedInputStream(in);
            }
            in = null;

            String actualCharset;
            if (!preferEmbeddedCharset && charset != null) {
                // 指定されている文字コードを使う。
                actualCharset = charset;
            } else {
                // metaタグで指定されている文字コードを使う。
                bis.mark(PREFETCH_BUFSIZE);
                byte[] buf = new byte[PREFETCH_BUFSIZE];
                bis.read(buf);
                bis.reset();
                actualCharset = detectHTMLEncoding(
                    new String(buf, "ISO-8859-1"), charset);
                if (actualCharset == null) {
                    actualCharset = "ISO-8859-1";
                }
            }
            return IOUtils.readString(
                new InputStreamReader(bis, actualCharset), false);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignore) {
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (Throwable ignore) {
                }
            }
        }
    }


    static String detectHTMLEncoding(String string, String defaultEncoding)
    {
        for (int i = 0; i < META_PATTERNS.length; i++) {
            Matcher matcher = META_PATTERNS[i].matcher(string);
            if (matcher.find()) {
                return matcher.group(3);
            }
        }
        return defaultEncoding;
    }


    public static String trimQueryAndParameter(String url)
    {
        if (url == null) {
            return null;
        }
        int idx = url.indexOf(';');
        if (idx < 0) {
            idx = url.indexOf('?');
        }
        if (idx >= 0) {
            return url.substring(0, idx);
        } else {
            return url;
        }
    }


    public static String addClassName(String element, String className)
    {
        int status = 0;
        for (int i = 0; i < element.length(); i++) {
            char ch = element.charAt(i);
            switch (status) {
            case 0:
                if (ch == ' ') {
                    status = 1;
                } else if (ch == '\'') {
                    status = 3;
                } else if (ch == '"') {
                    status = 4;
                }
                break;

            case 1:
                if (ch != ' ') {
                    if (element.substring(i).startsWith(PREFIX_CLASS)) {
                        // class属性が見つかった。
                        i += PREFIX_CLASS.length() + 1;
                        char quote = element.charAt(i - 1);
                        int pre = i;
                        for (; i < element.length()
                            && (ch = element.charAt(i)) != quote; i++) {
                            switch (status) {
                            case 1:
                                if (ch == ' ') {
                                    if (className.equals(element.substring(pre,
                                        i))) {
                                        // 指定したクラスが既に含まれているので何もしない。
                                        return element;
                                    } else {
                                        status = 2;
                                    }
                                }
                                break;
                            case 2:
                                if (ch != ' ') {
                                    pre = i;
                                    status = 1;
                                }
                                break;

                            default:
                                throw new IllegalStateException("Logic error");
                            }
                        }
                        if (status == 1 && pre < i
                            && className.equals(element.substring(pre, i))) {
                            // 指定したクラスが既に含まれているので何もしない。
                            return element;
                        } else {
                            if (pre == i) {
                                // class=""
                                return element.substring(0, i) + className
                                    + element.substring(i);
                            } else {
                                // class="value"
                                return element.substring(0, i) + " "
                                    + className + element.substring(i);
                            }
                        }
                    }
                    status = 0;
                }
                break;

            case 3:
                if (ch == '\'') {
                    status = 1;
                }
                break;

            case 4:
                if (ch == '"') {
                    status = 1;
                }
                break;

            default:
                throw new IllegalStateException("Logic error");
            }
        }

        // class属性が見つからなかった。
        int i = element.length() - 2;
        if (element.charAt(i) == '/') {
            // <.../>
            if (element.charAt(i - 1) == ' ') {
                // <... />
                return element.substring(0, i) + "class=\"" + className + "\" "
                    + element.substring(i);
            } else {
                // <.../>
                return element.substring(0, i) + " class=\"" + className
                    + "\" " + element.substring(i);
            }
        } else {
            // <...>
            i++;
            return element.substring(0, i) + " class=\"" + className + "\""
                + element.substring(i);
        }
    }


    /**
     * 指定されたURL中の非ASCIIコードに関してのみ
     * URLエンコードを行ないます。
     * <p>このメソッドは{@link #reencode(String, String)}でエンコーディングとして
     * UTF-8を指定したのと同じです。
     * </p>
     *
     * @param url エンコードするURL。
     * @return エンコードしたURL。
     */
    public static String reencode(String url)
    {
        return reencode(url, "UTF-8");
    }


    /**
     * 指定されたURL中の非ASCIIコードに関してのみ
     * URLエンコードを行ないます。
     * <p>エンコード後の文字エンコーディングは<code>encoding</code>で指定されたものになります。</p>
     * <p>非ASCIIコード部分以外はエンコードされません。
     * 従って '%' 等はそのままになります。</p>
     *
     * @param url エンコードするURL。
     * @param encoding エンコーディング。
     * @return エンコードしたURL。
     */
    public static String reencode(String url, String encoding)
    {
        if (url == null) {
            return null;
        }

        try {
            StringBuffer sb = new StringBuffer();
            int n = url.length();
            for (int i = 0; i < n; i++) {
                char ch = url.charAt(i);
                if (ch > 0x7f) {
                    byte[] bytes = url.substring(i, i + 1).getBytes(encoding);
                    for (int j = 0; j < bytes.length; j++) {
                        int b = bytes[j];
                        if (b < 0) {
                            b += 256;
                        }
                        sb.append("%");
                        sb.append(byteTable_[b / 16]);
                        sb.append(byteTable_[b % 16]);
                    }
                } else {
                    sb.append(ch);
                }
            }

            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Can't happen!");
        }
    }
}
