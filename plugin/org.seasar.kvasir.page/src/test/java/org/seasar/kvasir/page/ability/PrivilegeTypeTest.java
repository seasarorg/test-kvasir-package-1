package org.seasar.kvasir.page.ability;

import junit.framework.TestCase;


public class PrivilegeTypeTest extends TestCase
{
    public void testGetType_String()
        throws Exception
    {
        assertSame(PrivilegeType.ACCESS, PrivilegeType.getType("access"));
        assertSame(PrivilegeType.UNKNOWN, PrivilegeType.getType("unknown"));
    }


    public void testGetType_int()
        throws Exception
    {
        assertSame(PrivilegeType.ACCESS, PrivilegeType
            .getType(PrivilegeType.ACCESS.getValue()));
        assertSame(PrivilegeType.UNKNOWN, PrivilegeType
            .getType(PrivilegeType.UNKNOWN.getValue()));
    }
}
