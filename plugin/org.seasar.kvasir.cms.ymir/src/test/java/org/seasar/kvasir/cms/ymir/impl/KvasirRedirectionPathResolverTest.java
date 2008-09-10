package org.seasar.kvasir.cms.ymir.impl;

import junit.framework.TestCase;

import org.seasar.cms.ymir.mock.MockRequest;


public class KvasirRedirectionPathResolverTest extends TestCase
{
    private KvasirRedirectionPathResolver target_ = new KvasirRedirectionPathResolver();


    private MockRequest prepareRequest()
    {
        return new MockRequest() {
            @Override
            public Object getAttribute(String name)
            {
                if ("org.seasar.kvasir.cms.contextPath".equals(name)) {
                    return "";
                } else {
                    return null;
                }
            }
        }.setContextPath("/context");
    }


    private MockRequest prepareRequest2()
    {
        return new MockRequest() {
            @Override
            public Object getAttribute(String name)
            {
                if ("org.seasar.kvasir.cms.contextPath".equals(name)) {
                    return "/original";
                } else {
                    return null;
                }
            }
        }.setContextPath("/original/context").setPath("/index.html");
    }


    public void testResolve1()
        throws Exception
    {
        assertEquals("/context/", target_.resolve("", prepareRequest()));
    }


    public void testResolve2()
        throws Exception
    {
        assertEquals("/context/hoe.do", target_.resolve("/hoe.do",
            prepareRequest()));
    }


    public void testResolve3()
        throws Exception
    {
        assertEquals("/context/?hoe=fuga", target_.resolve("?hoe=fuga",
            prepareRequest()));
    }


    public void testResolve4()
        throws Exception
    {
        assertEquals("/", target_.resolve("!", prepareRequest()));
    }


    public void testResolve5()
        throws Exception
    {
        assertEquals("/hoe.do", target_.resolve("!/hoe.do", prepareRequest()));
    }


    public void testResolve6()
        throws Exception
    {
        assertEquals("/?hoe=fuga", target_.resolve("!?hoe=fuga",
            prepareRequest()));
    }


    public void testResolve7()
        throws Exception
    {
        assertEquals("/;jsessionid=XXXX", target_.resolve("!;jsessionid=XXXX",
            prepareRequest()));
    }


    public void testResolve8()
        throws Exception
    {
        assertEquals("/context/;jsessionid=XXXX", target_.resolve(
            ";jsessionid=XXXX", prepareRequest()));
    }


    public void testResolve9()
        throws Exception
    {
        assertEquals("/original/", target_.resolve("!", prepareRequest2()));
    }


    public void testResolve10()
        throws Exception
    {
        assertEquals("/original/?hoe=fuga", target_.resolve("!?hoe=fuga",
            prepareRequest2()));
    }


    public void testResolve11()
        throws Exception
    {
        assertEquals("/original/;jsessionid=XXXX", target_.resolve(
            "!;jsessionid=XXXX", prepareRequest2()));
    }


    public void testResolve12()
        throws Exception
    {
        assertEquals("/original/context/index.html", target_.resolve(".",
            prepareRequest2()));
    }
}
