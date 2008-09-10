package org.seasar.kvasir.util;

import junit.framework.TestCase;


public class MimeUtilsTest extends TestCase
{
    public void testGetCharset()
        throws Exception
    {
        assertNull(MimeUtils.getCharset(null));

        assertNull(MimeUtils.getCharset("text/html"));

        assertEquals("UTF-8", MimeUtils.getCharset("text/html;charset=UTF-8"));

        assertEquals("UTF-8", MimeUtils.getCharset("text/html; charset=UTF-8"));
    }


    public void testConstructContentType()
        throws Exception
    {
        assertNull("mediaTypeがnullの場合はnullを返すこと", MimeUtils
            .constructContentType(null, "UTF-8"));

        assertEquals("mediaTypeがtextでない場合はcharset指定がつかないこと",
            "application/octet-stream", MimeUtils.constructContentType(
                "application/octet-stream", "UTF-8"));

        assertEquals("charsetがnullの場合はcharset指定がつかないこと", "text/plain",
            MimeUtils.constructContentType("text/plain", null));

        assertEquals("charsetが空文字列の場合はcharset指定がつかないこと", "text/plain",
            MimeUtils.constructContentType("text/plain", ""));

        assertEquals("charset指定がつくこと", "text/plain; charset=UTF-8", MimeUtils
            .constructContentType("text/plain", "UTF-8"));
    }
}
