package org.seasar.kvasir.cms.processor.impl;

import org.seasar.kvasir.webapp.mock.MockHttpServletRequest;

import junit.framework.TestCase;


public class VirtualHttpServletRequestTest extends TestCase
{
    public void testGetServletPathLength()
        throws Exception
    {
        VirtualHttpServletRequest target = new VirtualHttpServletRequest(
            new MockHttpServletRequest());

        // nullの時は"/*"と同じ扱いにする。
        assertEquals(0, target.getServletPathLength(null));
        // "/"の時は
        //   ""   -> "/", null
        //   "/a" -> "/a", null
        assertEquals(VirtualHttpServletRequest.LENGTH_WHOLE, target
            .getServletPathLength("/"));
        // "/*"の時は
        //   ""   -> "", "/"
        //   "/a" -> "", "/a"
        assertEquals(0, target.getServletPathLength("/*"));
        // "/hoe"の時は
        //   "/hoe"   -> "/hoe", null
        //   "/hoe/a" -> （マッチしない）
        assertEquals(VirtualHttpServletRequest.LENGTH_WHOLE, target
            .getServletPathLength("/hoe"));
        // "/hoe/*"の時は
        //   "/hoe"   -> "/hoe", null
        //   "/hoe/a" -> "/hoe", "/a"
        assertEquals(4, target.getServletPathLength("/hoe/*"));
        // "*.hoe"の時は
        //   "/a.hoe"   -> "/a.hoe", null
        //   "/a.hoe/a" -> （マッチしない）
        assertEquals(VirtualHttpServletRequest.LENGTH_WHOLE, target
            .getServletPathLength("*.hoe"));
    }
}
