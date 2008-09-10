package org.seasar.kvasir.cms.ymir.constraint;

import org.seasar.cms.ymir.Constraint;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;


abstract public class AbstractPermissionConstraint
    implements Constraint
{
    private AuthPlugin pageAuthPlugin_;


    public AbstractPermissionConstraint(AuthPlugin pageAuthPlugin)
    {
        pageAuthPlugin_ = pageAuthPlugin;
    }


    protected User getCurrentActor()
    {
        return pageAuthPlugin_.getCurrentActor();
    }
}
