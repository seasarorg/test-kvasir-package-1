package org.seasar.kvasir.util.wiki;

import junit.framework.TestCase;


public class WikiEngineTest extends TestCase
{
    private static final String SP = System.getProperty("line.separator");


    /*
     * don't know whether the spec allows a nested link or not, the spec
     * that the current kvasir-wiki implementation conforms to.
     * it must be hard to support it with the current 'evaluation and dump' strategy I think.
     */
    public void testEvaluate_NestedLinkIsNotSupported()
        throws Exception
    {
        WikiEngine engine = new WikiEngine();
        engine.registerLineEvaluator(new LinkEvaluator());
        engine.start();
        String source = "[[[[ALT|image.jpg]]|http://localhost/target.html]]";
        String actual = engine.evaluate(source);
        assertEquals(
            "<p><img alt=\"[[ALT\" src=\"image.jpg\" />|http://localhost/target.html]]</p>"
                + SP, actual);
    }


    public void testEvaluate_LineEvaluater()
        throws Exception
    {
        WikiEngine engine = new WikiEngine();
        engine.registerLineEvaluator(new LinkEvaluator());
        engine.start();
        String source = "foo\r\n\r\n [[bar]]baz[[qux]] quxx\r\n\r\nquxxx";
        String actual = engine.evaluate(source);
        assertEquals("<p>foo</p>" + SP
            + "<p> <a href=\"bar\">bar</a>baz<a href=\"qux\">qux</a> quxx</p>"
            + SP + "<p>quxxx</p>" + SP, actual);
    }


    public void testEvaluate_HowCrlfTreated()
        throws Exception
    {
        WikiEngine engine = new WikiEngine();
        engine.registerLineEvaluator(new LinkEvaluator());
        engine.start();

        // with \n
        String source = "foo\n\nbar";
        String actual = engine.evaluate(source);
        assertEquals("<p>foo</p>" + SP + "<p>bar</p>" + SP, actual);

        // with \r\n; same as \n
        source = "foo\r\n\r\nbar";
        actual = engine.evaluate(source);
        assertEquals("<p>foo</p>" + SP + "<p>bar</p>" + SP, actual);
    }
}
