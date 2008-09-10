package org.seasar.kvasir.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class MimeUtils
{
    private static final String PREFIX_CHARSET = "charset=";

    private static final Set XML_MEDIATYPE_SET;

    private static final Set HTML_MEDIATYPE_SET;

    static {
        Set set = new HashSet();
        set.add("text/html");
        set.add("application/xhtml+xml");
        set.add("application/xml");
        set.add("text/xml");
        set.add("application/rss+xml");
        XML_MEDIATYPE_SET = Collections.unmodifiableSet(set);

        set = new HashSet();
        set.add("text/html");
        set.add("application/xhtml+xml");
        set.add("application/xml");
        set.add("text/xml");
        HTML_MEDIATYPE_SET = Collections.unmodifiableSet(set);
    }


    protected MimeUtils()
    {
    }


    public static String getCharset(String contentType)
    {
        if (contentType == null) {
            return null;
        }
        int semi = contentType.indexOf(';');
        if (semi < 0) {
            return null;
        }
        String charset = contentType.substring(semi + 1).trim();
        if (!charset.startsWith(PREFIX_CHARSET)) {
            return null;
        }
        return charset.substring(PREFIX_CHARSET.length()).trim();
    }


    public static String constructContentType(String mediaType, String charset)
    {
        if (mediaType == null) {
            return null;
        }

        String contentType;
        if (mediaType.startsWith("text/") && charset != null
            && charset.length() > 0) {
            contentType = mediaType + "; charset=" + charset;
        } else {
            contentType = mediaType;
        }
        return contentType;
    }


    public static boolean isXML(String mediaType)
    {
        return XML_MEDIATYPE_SET.contains(mediaType);
    }


    public static boolean isHTML(String mediaType)
    {
        return HTML_MEDIATYPE_SET.contains(mediaType);
    }


    public static boolean isText(String mediaType)
    {
        return mediaType != null && mediaType.startsWith("text/");
    }
}
