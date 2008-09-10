package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class RoleOfUserRow
{
    private int id_;

    private String pathname_;

    private boolean userInRole_;


    public RoleOfUserRow(User user, Role role)
    {
        id_ = role.getId();
        pathname_ = (user.getHeimId() == role.getHeimId()) ? role.getPathname()
            : "/" + role.getPathname();
        userInRole_ = role.contains(user);
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public boolean isUserInRole()
    {
        return userInRole_;
    }
}
