package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class UserOfRoleRow
{
    private int id_;

    private String pathname_;

    private boolean inRole_;


    public UserOfRoleRow(Role role, User user)
    {
        id_ = user.getId();
        pathname_ = (role.getHeimId() == user.getHeimId()) ? user.getPathname()
            : "/" + user.getPathname();
        inRole_ = role.contains(user);
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public boolean isInRole()
    {
        return inRole_;
    }
}
