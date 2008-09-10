package org.seasar.kvasir.cms.webdav.setting;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class MatchTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        Match actual = new Match();
        actual.setPath("PATH");
        assertBeanEquals("<match path=\"PATH\" />", actual);
    }


    public void testIsMatched()
        throws Exception
    {
        Match actual = new Match();
        actual.setPath("/path/to/dir");
        assertTrue(actual.isMatched("/path/to/dir"));
        assertTrue(actual.isMatched("/path/to/dir/hoe"));
        assertFalse(actual.isMatched("/path/to/dir1"));
        assertFalse(actual.isMatched("/path"));
    }


    public void testIsMatched2()
        throws Exception
    {
        Match actual = new Match();
        actual.setPath("/path/to/dir(|/.*)$");
        actual.setRegex(true);
        assertTrue(actual.isMatched("/path/to/dir"));
        assertTrue(actual.isMatched("/path/to/dir/hoe"));
        assertFalse(actual.isMatched("/path/to/dir1"));
        assertFalse(actual.isMatched("/path"));
    }
}
