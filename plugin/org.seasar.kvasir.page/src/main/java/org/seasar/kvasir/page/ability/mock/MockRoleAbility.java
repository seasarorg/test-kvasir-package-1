package org.seasar.kvasir.page.ability.mock;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.RoleAbility;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


public class MockRoleAbility extends MockPageAbility
    implements RoleAbility
{
    public MockRoleAbility(Page page)
    {
        super(page);
    }

    public boolean contains(User user)
    {
        return false;
    }

    public boolean contains(Group group)
    {
        return false;
    }

    public void depriveRoleFrom(User user)
    {
    }

    public void depriveRoleFrom(Group group)
    {
    }

    public void depriveRoleFromAllGroups()
    {
    }

    public void depriveRoleFromAllUsers()
    {
    }

    public Group[] getGroups()
    {
        return null;
    }

    public User[] getUsers()
    {
        return null;
    }

    public void giveRoleTo(User user)
    {
    }

    public void giveRoleTo(Group group)
    {
    }

    public boolean isUserInRole(User user)
    {
        return false;
    }
}
