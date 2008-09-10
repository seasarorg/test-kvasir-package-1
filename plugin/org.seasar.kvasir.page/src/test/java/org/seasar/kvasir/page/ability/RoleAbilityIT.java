package org.seasar.kvasir.page.ability;

import junit.framework.Test;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class RoleAbilityIT extends KvasirPluginTestCase<PagePlugin>
{
    private PageAlfr pageAlfr_;


    @Override
    protected String getTargetPluginId()
    {
        return PagePlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(RoleAbilityIT.class);
    }


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        pageAlfr_ = getPlugin().getPageAlfr();
    }


    public void testIsUserInRole()
        throws Exception
    {
        User user = pageAlfr_.getPage(User.class, Page.ID_ANONYMOUS_USER);
        Role role = pageAlfr_.getPage(Role.class, Page.ID_ANONYMOUS_ROLE);

        assertTrue(role.isUserInRole(user));
    }
}
