package org.seasar.kvasir.cms.ymir.constraint;

import static org.seasar.kvasir.cms.CmsPlugin.ATTR_PAGEREQUEST;

import org.seasar.cms.ymir.ConstraintViolatedException;
import org.seasar.cms.ymir.PermissionDeniedException;
import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.auth.AuthPlugin;


public class HasPrivilegeConstraint extends AbstractPermissionConstraint
{
    private Privilege privilege_;


    public HasPrivilegeConstraint(Privilege privilege,
        AuthPlugin pageAuthPlugin)
    {
        super(pageAuthPlugin);

        privilege_ = privilege;
    }


    public void confirm(Object component, Request request)
        throws ConstraintViolatedException
    {
        Page page = getPage(request);
        PermissionAbility permission = page.getAbility(PermissionAbility.class);
        if (!permission.permits(getCurrentActor(), privilege_)) {
            throw new PermissionDeniedException(
                "Current user has no privilege to access page: " + page);
        }
    }


    Page getPage(Request request)
    {
        PageRequest pageRequest = (PageRequest)request
            .getAttribute(ATTR_PAGEREQUEST);
        return pageRequest.getMy().getNearestPage();
    }
}
