package org.seasar.kvasir.cms.ymir.constraint;

import junit.framework.TestCase;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.auth.mock.MockAuthPlugin;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.mock.MockPageAlfr;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.mock.MockRole;
import org.seasar.kvasir.page.type.mock.MockUser;


public class InRoleConstraintTest extends TestCase
{
    private InRoleConstraint target_;

    private User user_ = new MockUser(101, PathId.HEIM_MIDGARD, "/users/user1");

    private Page gardRootPage_ = new MockPage(102, PathId.HEIM_MIDGARD, "/gard") {
        @SuppressWarnings("unchecked")
        @Override
        public <P extends Page> P getChild(Class<P> clazz, String name)
        {
            if ("/roles/role2".equals(name)) {
                return (P)new MockRole(102, PathId.HEIM_MIDGARD,
                    "/gard/roles/role2");
            } else {
                return null;
            }
        }
    };


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        target_ = new InRoleConstraint(new String[] { "/roles/role1",
            "@/roles/role2" }, new MockPageAlfr() {
            @SuppressWarnings("unchecked")
            @Override
            public <P extends Page> P getPage(Class<P> clazz, int heimId,
                String pathname)
            {
                if ("/roles/role1".equals(pathname)) {
                    return (P)new MockRole(100, PathId.HEIM_MIDGARD,
                        "/roles/role1");
                } else {
                    return null;
                }
            }
        }, new MockAuthPlugin() {
            @Override
            public User getCurrentActor()
            {
                return user_;
            }
        });
    }


    public void testConfirm_relativePath()
        throws Exception
    {
        try {
            assertFalse(target_.confirm("/roles/role1", user_, gardRootPage_));
            fail("ロールでないパスを指定した場合は例外がスローされること");
        } catch (IllegalArgumentException expected) {
        }
        assertFalse("正しく権限を確認できること", target_.confirm("/roles/role2", user_,
            gardRootPage_));
    }


    public void testConfirm_absolutePath()
        throws Exception
    {
        try {
            assertFalse(target_.confirm("/roles/role2", user_,
                PathId.HEIM_MIDGARD));
            fail("ロールでないパスを指定した場合は例外がスローされること");
        } catch (IllegalArgumentException expected) {
        }
        assertFalse("正しく権限を確認できること", target_.confirm("/roles/role1", user_,
            PathId.HEIM_MIDGARD));
    }
}
