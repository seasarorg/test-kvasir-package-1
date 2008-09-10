package org.seasar.kvasir.cms.ymir.constraint;

import junit.framework.TestCase;

import org.seasar.cms.ymir.PermissionDeniedException;
import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.ability.mock.MockPermissionAbility;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.mock.MockUser;


public class HasPrivilegeConstraintTest extends TestCase
{
    private HasPrivilegeConstraint target_ = new HasPrivilegeConstraint(
        Privilege.ACCESS, null) {
        @Override
        protected User getCurrentActor()
        {
            return user_;
        }


        @Override
        Page getPage(Request request)
        {
            return page_;
        }
    };

    private Page page_ = new MockPage(100, PathId.HEIM_MIDGARD, "/page") {
        @SuppressWarnings("unchecked")
        @Override
        public <P extends PageAbility> P getAbility(Class<P> key)
        {
            if (key == PermissionAbility.class) {
                return (P)new MockPermissionAbility(this) {
                    @Override
                    public boolean permits(User user, Privilege priv)
                    {
                        if (user.getId() == 101) {
                            return (priv.getLevel().getValue() <= PrivilegeLevel.VIEW
                                .getValue());
                        } else {
                            return false;
                        }
                    }
                };
            } else {
                return null;
            }
        }
    };

    private User user_ = new MockUser(101, PathId.HEIM_MIDGARD, "/users/user1");


    public void testConfirm1()
        throws Exception
    {
        try {
            target_.confirm(null, null);
            fail("正しく権限を確認できること");
        } catch (PermissionDeniedException expected) {
        }
    }


    public void testConfirm2()
        throws Exception
    {
        target_ = new HasPrivilegeConstraint(Privilege.ACCESS_VIEW, null) {
            @Override
            protected User getCurrentActor()
            {
                return user_;
            }


            @Override
            Page getPage(Request request)
            {
                return page_;
            }
        };

        try {
            target_.confirm(null, null);
        } catch (PermissionDeniedException ex) {
            fail("正しく権限を確認できること");
        }
    }
}
