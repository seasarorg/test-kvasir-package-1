package org.seasar.kvasir.page.ability;

import junit.framework.TestCase;


public class PrivilegeLevelTest extends TestCase
{
    public void testSpike()
        throws Exception
    {
        try {
            Enum1.valueOf("a");
            fail("toString()が返す文字列ではvalueOfできない");
        } catch (IllegalArgumentException ex) {
        }
    }


    public void testGetLevel_String()
        throws Exception
    {
        assertSame(PrivilegeLevel.ACCESS, PrivilegeLevel.getLevel("access"));
        assertSame(PrivilegeLevel.COMMENT, PrivilegeLevel.getLevel("comment"));
        assertSame(PrivilegeLevel.NEVER, PrivilegeLevel.getLevel("never"));
        assertSame(PrivilegeLevel.NONE, PrivilegeLevel.getLevel("none"));
        assertSame(PrivilegeLevel.PEEK, PrivilegeLevel.getLevel("peek"));
        assertSame(PrivilegeLevel.UNKNOWN, PrivilegeLevel.getLevel("unknown"));
    }


    public void testGetLevel_int()
        throws Exception
    {
        assertSame(PrivilegeLevel.ACCESS, PrivilegeLevel
            .getLevel(PrivilegeLevel.ACCESS.getValue()));
        assertSame(PrivilegeLevel.COMMENT, PrivilegeLevel
            .getLevel(PrivilegeLevel.COMMENT.getValue()));
        assertSame(PrivilegeLevel.NEVER, PrivilegeLevel
            .getLevel(PrivilegeLevel.NEVER.getValue()));
        assertSame(PrivilegeLevel.NONE, PrivilegeLevel
            .getLevel(PrivilegeLevel.NONE.getValue()));
        assertSame(PrivilegeLevel.PEEK, PrivilegeLevel
            .getLevel(PrivilegeLevel.PEEK.getValue()));
        assertSame(PrivilegeLevel.UNKNOWN, PrivilegeLevel
            .getLevel(PrivilegeLevel.UNKNOWN.getValue()));
    }
}
