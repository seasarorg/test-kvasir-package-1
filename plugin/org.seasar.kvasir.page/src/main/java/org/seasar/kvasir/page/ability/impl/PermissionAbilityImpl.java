package org.seasar.kvasir.page.ability.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.PermissionAbilityAlfr;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeType;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PermissionAbilityImpl extends AbstractPageAbility
    implements PermissionAbility
{
    private PermissionAbilityAlfr       alfr_;


    public PermissionAbilityImpl(PermissionAbilityAlfr alfr, Page page)
    {
        super(alfr, page);
        alfr_ = alfr;
    }


    public boolean permits(User user, Privilege priv)
    {
        return alfr_.permits(page_, user, priv);
    }


    public Permission[] getPermissions()
    {
        return alfr_.getPermissions(page_);
    }


    public void setPermissions(Permission[] permissions)
    {
        alfr_.setPermissions(page_, permissions);
    }


    public void clearPermissions()
    {
        alfr_.clearAttributes(page_);
    }


    public void grantPrivilege(Role role, Privilege priv)
    {
        alfr_.grantPrivilege(page_, role, priv);
    }


    public void revokePrivilege(Role role, PrivilegeType privType)
    {
        alfr_.revokePrivilege(page_, role, privType);
    }


    public void revokeAllPrivileges(Role role)
    {
        alfr_.revokeAllPrivileges(page_, role);
    }
}
