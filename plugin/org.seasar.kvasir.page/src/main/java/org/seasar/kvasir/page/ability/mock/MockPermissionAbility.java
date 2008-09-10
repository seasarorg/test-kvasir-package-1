package org.seasar.kvasir.page.ability.mock;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeType;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class MockPermissionAbility extends MockPageAbility
    implements PermissionAbility
{
    public MockPermissionAbility(Page page)
    {
        super(page);
    }


    public void clearPermissions()
    {
    }


    public Permission[] getPermissions()
    {
        return null;
    }


    public void grantPrivilege(Role role, Privilege priv)
    {
    }


    public boolean permits(User user, Privilege priv)
    {
        return false;
    }


    public void revokeAllPrivileges(Role role)
    {
    }


    public void revokePrivilege(Role role, PrivilegeType privType)
    {
    }


    public void setPermissions(Permission[] permissions)
    {
    }
}
