package org.seasar.kvasir.util.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;


public class EncodingUtilsTest extends TestCase
{
    public void testGetHTMLEncoding1()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(
                "<html>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />\r\n</html>\r\n"
                    .getBytes("ISO-8859-1")));

        assertEquals("UTF-8", EncodingUtils.getHTMLEncoding(bis));
    }


    public void testGetHTMLEncoding2()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(
                "<html>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n</html>\r\n"
                    .getBytes("ISO-8859-1")));

        assertEquals("UTF-8", EncodingUtils.getHTMLEncoding(bis));
    }


    public void testGetHTMLEncoding3()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(
                "<html>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n</html>\r\n"
                    .getBytes("ISO-8859-1")));

        assertEquals("utf-8", EncodingUtils.getHTMLEncoding(bis));
    }


    public void testGetHTMLEncoding4()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(
                "<html>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html\" />\r\n</html>\r\n"
                    .getBytes("ISO-8859-1")));

        assertEquals("ISO-8859-1", EncodingUtils.getHTMLEncoding(bis));
    }


    public void testGetHTMLEncoding5()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream("<html>\r\n</html>\r\n"
                .getBytes("ISO-8859-1")));

        assertEquals("ISO-8859-1", EncodingUtils.getHTMLEncoding(bis));
    }


    public void testGetHTMLEncoding6()
        throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(
                "<html>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n</html>\r\n"
                    .getBytes("ISO-8859-1")));

        assertEquals("UTF-8", EncodingUtils.getHTMLEncoding(bis));
    }

}
