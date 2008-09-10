package org.seasar.kvasir.page.ability;

import junit.framework.TestCase;


public class PrivilegeTest extends TestCase
{
    public void testGetPrivilege_String()
        throws Exception
    {
        assertSame(Privilege.ACCESS, Privilege.getPrivilege("access"));
        assertNull(Privilege.getPrivilege("access.access"));
        assertSame(Privilege.ACCESS_COMMENT, Privilege
            .getPrivilege("access.comment"));
        assertSame(Privilege.ACCESS_NEVER, Privilege
            .getPrivilege("access.never"));
        assertSame(Privilege.ACCESS_NONE, Privilege.getPrivilege("access.none"));
        assertSame(Privilege.ACCESS_PEEK, Privilege.getPrivilege("access.peek"));
        assertSame(Privilege.ACCESS_VIEW, Privilege.getPrivilege("access.view"));
    }


    public void testGetPrivilege_Type_Level()
        throws Exception
    {
        assertSame(Privilege.ACCESS, Privilege.getPrivilege(PrivilegeType.ACCESS,
            PrivilegeLevel.ACCESS));
        assertSame(Privilege.ACCESS_COMMENT, Privilege.getPrivilege(
            PrivilegeType.ACCESS, PrivilegeLevel.COMMENT));
        assertSame(Privilege.ACCESS_NEVER, Privilege.getPrivilege(PrivilegeType.ACCESS,
            PrivilegeLevel.NEVER));
        assertSame(Privilege.ACCESS_NONE, Privilege.getPrivilege(PrivilegeType.ACCESS,
            PrivilegeLevel.NONE));
        assertSame(Privilege.ACCESS_PEEK, Privilege.getPrivilege(PrivilegeType.ACCESS,
            PrivilegeLevel.PEEK));
        assertSame(Privilege.ACCESS_VIEW, Privilege.getPrivilege(PrivilegeType.ACCESS,
            PrivilegeLevel.VIEW));

        assertNull(Privilege.getPrivilege(PrivilegeType.ACCESS, null));
        assertNull(Privilege.getPrivilege(null, PrivilegeLevel.VIEW));
        assertNull(Privilege.getPrivilege((PrivilegeType)null, (PrivilegeLevel)null));
    }
}
