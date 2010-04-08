package org.seasar.kvasir.util;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class StringUtilsTest extends TestCase
{
    private static final String SP = System.getProperty("line.separator");


    public void testNormalizeLineSeparator()
        throws Exception
    {
        assertEquals("正しく正規化できること（Windows）", "a" + SP + "b" + SP, StringUtils
            .normalizeLineSeparator("a\r\nb\r\n"));
        assertEquals("正しく正規化できること（UNIX）", "a" + SP + "b" + SP, StringUtils
            .normalizeLineSeparator("a\nb\n"));

        assertEquals("空文字列を正しく正規化できること", "", StringUtils
            .normalizeLineSeparator(""));

        assertEquals("末尾に改行コードがない場合でも正しく正規化できること（Windows）", "a" + SP + "b",
            StringUtils.normalizeLineSeparator("a\r\nb"));
        assertEquals("末尾に改行コードがない場合でも正しく正規化できること（UNIX）", "a" + SP + "b",
            StringUtils.normalizeLineSeparator("a\nb"));
    }


    public void testReplace()
        throws Exception
    {
        assertNull("文字列としてnullを指定した場合はnullを返すこと", StringUtils.replace(null,
            "from", "to"));
        assertEquals("from文字列としてnullを指定した場合は元の文字列を返すこと", "abc", StringUtils
            .replace("abc", null, "to"));
        assertEquals("to文字列としてnullを指定した場合は空文字列を指定した時と同じ振る舞いをすること", "abc",
            StringUtils.replace("abfromc", "from", null));

        assertEquals("正しく置換されること", "abctodeto", StringUtils.replace(
            "abcfromdefrom", "from", "to"));
        assertEquals("正しく置換されること", "abcde", StringUtils.replace(
            "abcfromdefrom", "from", ""));
    }


    public void test_toHexString()
        throws Exception
    {
        assertEquals("ff0111", StringUtils.toHexString(new byte[] { (byte)-1,
            (byte)1, (byte)17 }));
    }


    public void test_toBytes()
        throws Exception
    {
        byte[] actual = StringUtils.toBytes("ff0111");

        int idx = 0;
        assertEquals((byte)-1, actual[idx++]);
        assertEquals((byte)1, actual[idx++]);
        assertEquals((byte)17, actual[idx++]);
    }
}
