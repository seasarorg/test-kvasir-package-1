package org.seasar.kvasir.cms.kdiary.kdiary;

import junit.framework.TestCase;


public class KDiaryUtilsTest extends TestCase
{
    public void testToJavaDateFormat()
        throws Exception
    {
        assertEquals("yyyy MM MMM MMMM dd EEE EEEE", KdiaryUtils
            .toJavaDateFormat("%Y %m %b %B %d %a %A"));
    }
}
