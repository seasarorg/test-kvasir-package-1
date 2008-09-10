package org.seasar.kvasir.page.type.mock;

import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class MockUser extends MockPage
    implements User
{
    private boolean administrator_;


    public MockUser(int id, String pathname)
    {
        this(id, PathId.HEIM_MIDGARD, pathname);
    }


    public MockUser(int id, int heimId, String pathname)
    {
        super(id, heimId, pathname);
    }


    public Group[] getGroups()
    {
        return null;
    }


    public String[] getMailAddresses()
    {
        return null;
    }


    public String getPassword()
    {
        return null;
    }


    public Role[] getRoles()
    {
        return null;
    }


    public boolean isAdministrator()
    {
        return administrator_;
    }


    public MockUser setAdministrator(boolean administrator)
    {
        administrator_ = administrator;
        return this;
    }


    public boolean isAnonymous()
    {
        return false;
    }


    public void setMailAddresses(String[] mailAddresses)
    {
    }


    public void setPassword(String password)
    {
    }
}
