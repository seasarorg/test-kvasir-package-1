package org.seasar.kvasir.cms.zpt.util;

import junit.framework.TestCase;


public class HTMLUtilsTest extends TestCase
{
    public void testInnerHTML()
        throws Exception
    {
        String actual = HTMLUtils
            .innerHTML(HTMLUtils
                .getElementById(
                    "<html><body><div id=\"a\"><div id=\"b\"><div id=\"c\">D</div></div></div></body></html>",
                    "b"));
        assertNotNull(actual);
        assertEquals("<div id=\"c\">D</div>", actual);
    }
}
