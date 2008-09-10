package org.seasar.kvasir.page.type.mock;

import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class MockRole extends MockPage
    implements Role
{

    public MockRole(int id, String pathname)
    {
        this(id, PathId.HEIM_MIDGARD, pathname);
    }


    public MockRole(int id, int heimId, String pathname)
    {
        super(id, heimId, pathname);
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
