package org.seasar.kvasir.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 文字エンコーディングのユーティリティメソッドを持つクラスです。
 * 
 * @author YOKOTA Takehiko
 */
public class EncodingUtils
{
    private static final int PREFETCH_BUFSIZE = 1024;

    private static final Pattern[] META_PATTERNS = new Pattern[] {
        Pattern
            .compile(
                "<meta\\s+http-equiv=(['\"])content-type\\1\\s+content=(['\"])[^;]*;\\s*charset=(.*?)\\2",
                Pattern.CASE_INSENSITIVE),
        Pattern
            .compile(
                "<meta\\s+http-equiv=content-type\\s+content=[^;]*;charse(t)(=)(.*?)[\\s|>|/]",
                Pattern.CASE_INSENSITIVE) };

    private static final String DEFAULT_XML_ENCODING = "UTF-8";

    private static final String DEFAULT_HTML_ENCODING = "ISO-8859-1";


    public static String getCharsetFromContentType(String contentType)
    {
        int semi = contentType.indexOf(";");
        if (semi >= 0) {
            String subpart = contentType.substring(semi + 1).trim();
            int equal = subpart.indexOf("=");
            if (equal >= 0
                && subpart.substring(0, equal).trim().equalsIgnoreCase(
                    "charset")) {
                return subpart.substring(equal + 1).trim();
            }
        }
        return null;
    }


    public static String getXMLEncoding(BufferedInputStream bis)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        LOOP: for (int count = 0;; count++) {
            bis.mark(PREFETCH_BUFSIZE * (count + 1));
            bis.skip(PREFETCH_BUFSIZE * count);

            for (int i = 0; i < PREFETCH_BUFSIZE; i++) {
                int b = bis.read();
                if (b < 0 || b == 0x0a || b == 0x0d) {
                    bis.reset();
                    break LOOP;
                } else {
                    bos.write((byte)b);
                }
            }

            bis.reset();
        }

        bos.flush();
        String line;
        try {
            line = new String(baos.toByteArray(), "ISO_8859_1");
        } catch (UnsupportedEncodingException ex) {
            line = new String(baos.toByteArray());
        }

        String encoding = DEFAULT_XML_ENCODING;
        if (line.startsWith("<?")) {
            int beginquote = line.indexOf(" encoding=\"");
            if (beginquote >= 0) {
                beginquote += 11/*= " encoding=\"".length() */;
                int endquote = line.indexOf("\"", beginquote);
                if (endquote >= 0) {
                    encoding = line.substring(beginquote, endquote).trim();
                }
            }
        }

        return encoding;
    }


    public static String getHTMLEncoding(BufferedInputStream bis)
        throws IOException
    {
        bis.mark(PREFETCH_BUFSIZE);
        byte[] buf = new byte[PREFETCH_BUFSIZE];
        bis.read(buf);
        bis.reset();
        String encoding = detectHTMLEncoding(new String(buf, "ISO-8859-1"));
        if (encoding == null) {
            encoding = DEFAULT_HTML_ENCODING;
        }
        return encoding;
    }


    static String detectHTMLEncoding(String string)
    {
        for (int i = 0; i < META_PATTERNS.length; i++) {
            Matcher matcher = META_PATTERNS[i].matcher(string);
            if (matcher.find()) {
                return matcher.group(3);
            }
        }
        return null;
    }
}
