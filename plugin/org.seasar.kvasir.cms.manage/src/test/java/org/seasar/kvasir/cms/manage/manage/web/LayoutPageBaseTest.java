package org.seasar.kvasir.cms.manage.manage.web;

import junit.framework.TestCase;

import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockServletContextImpl;
import org.seasar.kvasir.cms.CmsPlugin;


public class LayoutPageBaseTest extends TestCase
{
    private LayoutPageBase target_ = new LayoutPageBase() {};


    public void testGetStartPathname()
        throws Exception
    {
        target_.setStart("");
        assertEquals("", target_.getStartPathname());

        target_.setStart("/path/to/page");
        assertEquals("/path/to/page", target_.getStartPathname());

        target_.setStart("http://localhost:8080/context/path/to/page");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
            new MockServletContextImpl("/context/manage"), "/layout.do");
        request.setAttribute(CmsPlugin.ATTR_CONTEXT_PATH, "/context");
        target_.setHttpRequest(request);
        assertEquals("コンテキストパスが空文字列でない場合", "/path/to/page", target_
            .getStartPathname());

        target_.setStart("http://localhost:8080/path/to/page");
        request = new MockHttpServletRequestImpl(new MockServletContextImpl(
            "/context/manage"), "/layout.do");
        request.setAttribute(CmsPlugin.ATTR_CONTEXT_PATH, "");
        target_.setHttpRequest(request);
        assertEquals("コンテキストパスが空文字列の場合", "/path/to/page", target_
            .getStartPathname());
    }
}
