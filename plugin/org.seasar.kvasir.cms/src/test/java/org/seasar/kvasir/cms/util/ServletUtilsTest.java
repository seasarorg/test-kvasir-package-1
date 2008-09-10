package org.seasar.kvasir.cms.util;

import junit.framework.TestCase;

import org.seasar.kvasir.webapp.mock.MockHttpServletRequest;


public class ServletUtilsTest extends TestCase
{
    public void testGetWebappURL()
        throws Exception
    {
        assertEquals("http://www.example.com:8080/path", ServletUtils
            .getWebappURL(prepareMockHttpServletRequest()));
    }


    public void testGetRequestURL()
        throws Exception
    {
        assertEquals("http://www.example.com:8080/path/path/to/other/page",
            ServletUtils.getURL("/path/to/other/page",
                prepareMockHttpServletRequest()));
    }


    protected MockHttpServletRequest prepareMockHttpServletRequest()
    {
        return new MockHttpServletRequest() {
            @Override
            public StringBuffer getRequestURL()
            {
                return new StringBuffer(
                    "http://www.example.com:8080/path/to/page");
            }


            @Override
            public String getRequestURI()
            {
                return "/path/to/page";
            }


            @Override
            public String getContextPath()
            {
                return "/path";
            }
        };
    }
}
