package org.seasar.kvasir.page;

import junit.framework.Test;

import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.ability.PermissionAbilityAlfr;
import org.seasar.kvasir.page.ability.PropertyAbilityAlfr;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.type.DirectoryPageType;
import org.seasar.kvasir.page.type.GroupPageType;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.RolePageType;
import org.seasar.kvasir.page.type.UserPageType;


/**
 * @author YOKOTA Takehiko
 */
public class PagePluginIT extends PagePluginITCase
{
    public static Test suite()
        throws Exception
    {
        return createTestSuite(PagePluginIT.class);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }


    public void testGetPageTypes()
        throws Exception
    {
        PageType[] actual = getPlugin().getPageTypes();
        assertNotNull(actual);
        assertEquals(5, actual.length);
    }


    public void testGetPageType()
        throws Exception
    {
        assertNotNull(getPlugin().getPageType(UserPageType.class));
        assertNotNull(getPlugin().getPageType(UserPageType.ID));
        assertNotNull(getPlugin().getPageType(GroupPageType.class));
        assertNotNull(getPlugin().getPageType(GroupPageType.ID));
        assertNotNull(getPlugin().getPageType(RolePageType.class));
        assertNotNull(getPlugin().getPageType(RolePageType.ID));
        assertNotNull(getPlugin().getPageType(DirectoryPageType.class));
        assertNotNull(getPlugin().getPageType(DirectoryPageType.ID));
    }


    public void testGetPageAbilityAlfrs()
        throws Exception
    {
        assertNotNull(getPlugin().getPageAbilityAlfr(PropertyAbilityAlfr.class));
        assertNotNull(getPlugin().getPageAbilityAlfr(GroupAbilityAlfr.class));
        assertNotNull(getPlugin().getPageAbilityAlfr(RoleAbilityAlfr.class));
        assertNotNull(getPlugin().getPageAbilityAlfr(
            PermissionAbilityAlfr.class));
    }


    public void testIsValidName()
        throws Exception
    {
        assertFalse(getPlugin().isValidName("."));
        assertFalse(getPlugin().isValidName("hoehoe/fugafuga"));
    }
}
