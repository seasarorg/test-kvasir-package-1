package org.seasar.kvasir.util.html;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;


public class HTMLUtilsTest extends TestCase
{
    public void testDetectEncoding()
        throws Exception
    {
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>",
                    null));
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head>",
                    null));
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><meta http-equiv=Content-Type content=text/html;charset=UTF-8 /></head>",
                    null));
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" /></head>",
                    null));
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>",
                    null));
        assertEquals(
            "UTF-8",
            HTMLUtils
                .detectHTMLEncoding(
                    "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>",
                    null));
        assertEquals("ISO-8859-1", HTMLUtils.detectHTMLEncoding("",
            "ISO-8859-1"));
        assertNull(HTMLUtils.detectHTMLEncoding("", null));
    }


    public void testReadHTML()
        throws Exception
    {
        assertEquals(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ",
            HTMLUtils.readHTML(new ByteArrayInputStream(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ"
                    .getBytes("UTF-8")), null, false));

        assertEquals(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ",
            HTMLUtils.readHTML(new ByteArrayInputStream(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ"
                    .getBytes("UTF-8")), null, true));

        assertEquals(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ",
            HTMLUtils.readHTML(new ByteArrayInputStream(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ"
                    .getBytes("UTF-16")), "UTF-16", false));

        assertEquals(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ",
            HTMLUtils.readHTML(new ByteArrayInputStream(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">あ"
                    .getBytes("UTF-8")), "UTF-16", true));

        assertEquals("あ", HTMLUtils.readHTML(new ByteArrayInputStream("あ"
            .getBytes("UTF-16")), "UTF-16", false));

        assertEquals("あ", HTMLUtils.readHTML(new ByteArrayInputStream("あ"
            .getBytes("UTF-16")), "UTF-16", true));

        assertEquals("\u00e3\u0081\u0082", HTMLUtils.readHTML(
            new ByteArrayInputStream("あ".getBytes("UTF-8")), null, false));

        assertEquals("\u00e3\u0081\u0082", HTMLUtils.readHTML(
            new ByteArrayInputStream("あ".getBytes("UTF-8")), null, true));
    }


    public void testTrimQueryAndParameter1()
        throws Exception
    {
        assertNull(HTMLUtils.trimQueryAndParameter(null));
    }


    public void testTrimQueryAndParameter2()
        throws Exception
    {
        assertEquals("/path/to/what", HTMLUtils
            .trimQueryAndParameter("/path/to/what"));
    }


    public void testTrimQueryAndParameter3()
        throws Exception
    {
        assertEquals("/path/to/what", HTMLUtils
            .trimQueryAndParameter("/path/to/what;jsessionid=HOEHOE"));
    }


    public void testTrimQueryAndParameter4()
        throws Exception
    {
        assertEquals("/path/to/what", HTMLUtils
            .trimQueryAndParameter("/path/to/what;jsessionid=HOEHOE?fuga=hehe"));
    }


    public void testTrimQueryAndParameter5()
        throws Exception
    {
        assertEquals("/path/to/what", HTMLUtils
            .trimQueryAndParameter("/path/to/what?fuga=hehe"));
    }


    public void testAddClassName()
        throws Exception
    {
        assertEquals("class属性が追加されること", "<img src=\"\" class=\"value\" />",
            HTMLUtils.addClassName("<img src=\"\" />", "value"));

        assertEquals("シングルクオートが含まれていても問題ないこと",
            "<img src='' class=\"value\" />", HTMLUtils.addClassName(
                "<img src='' />", "value"));

        assertEquals("エレメントの終わりが .../>でも問題ないこと",
            "<img src=\"\" class=\"value\" />", HTMLUtils.addClassName(
                "<img src=\"\"/>", "value"));

        assertEquals("エレメントの終わりが ...>でも問題ないこと",
            "<img src=\"\" class=\"value\">", HTMLUtils.addClassName(
                "<img src=\"\">", "value"));

        assertEquals("class属性が存在する場合は追加されること",
            "<img src=\"\" class=\"value1 value2\" />", HTMLUtils.addClassName(
                "<img src=\"\" class=\"value1\" />", "value2"));

        assertEquals("class属性が存在する場合に値がシングルクオートで囲われていても正しく処理が行なわれること",
            "<img src='' class='value1 value2' />", HTMLUtils.addClassName(
                "<img src='' class='value1' />", "value2"));

        assertEquals("既に存在する場合は何もしないこと", "<img src=\"\" class=\"value\" />",
            HTMLUtils.addClassName("<img src=\"\" class=\"value\" />", "value"));

        assertEquals("既に存在する場合に値がシングルクオートで囲われていても正しく処理が行なわれること",
            "<img src=\"\" class='value1 value2' />", HTMLUtils.addClassName(
                "<img src=\"\" class='value1' />", "value2"));
    }
}
