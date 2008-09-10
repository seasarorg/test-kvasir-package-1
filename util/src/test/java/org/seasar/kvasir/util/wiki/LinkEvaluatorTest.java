package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;


public class LinkEvaluatorTest extends TestCase
{
    public void testEvaluate()
        throws Exception
    {
        LinkEvaluator target = new LinkEvaluator();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        target.evaluate(new Context(null), pw,
            "TITLE|http://localhost/image.jpg");
        pw.flush();
        String actual = sw.toString();

        assertEquals(
            "<img alt=\"TITLE\" src=\"http://localhost/image.jpg\" />", actual);
    }


    public void testEvaluate_パラメータが指定されていても正しく画像へのリンクを生成できること()
        throws Exception
    {
        LinkEvaluator target = new LinkEvaluator();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        target.evaluate(new Context(null), pw,
            "TITLE|http://localhost/image.jpg;param=value");
        pw.flush();
        String actual = sw.toString();

        assertEquals(
            "<img alt=\"TITLE\" src=\"http://localhost/image.jpg;param=value\" />",
            actual);
    }


    public void testEvaluate_クエリ文字列が指定されていても正しく画像へのリンクを生成できること()
        throws Exception
    {
        LinkEvaluator target = new LinkEvaluator();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        target.evaluate(new Context(null), pw,
            "TITLE|http://localhost/image.jpg?param=value");
        pw.flush();
        String actual = sw.toString();

        assertEquals(
            "<img alt=\"TITLE\" src=\"http://localhost/image.jpg?param=value\" />",
            actual);
    }


    public void testEvaluate_クラス指定が正しくできること()
        throws Exception
    {
        LinkEvaluator target = new LinkEvaluator();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        target.evaluate(new Context(null), pw,
            "TITLE|http://localhost/image.jpg<left>");
        pw.flush();
        String actual = sw.toString();

        assertEquals(
            "<img alt=\"TITLE\" src=\"http://localhost/image.jpg\" class=\"left\" />",
            actual);
    }
}
