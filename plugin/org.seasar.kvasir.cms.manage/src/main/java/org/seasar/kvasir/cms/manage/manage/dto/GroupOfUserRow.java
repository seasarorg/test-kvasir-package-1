package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


public class GroupOfUserRow
{
    private int id_;

    private String pathname_;

    private boolean userInGroup_;


    public GroupOfUserRow(User user, Group group)
    {
        id_ = group.getId();
        pathname_ = (user.getHeimId() == group.getHeimId()) ? group
            .getPathname() : "/" + group.getPathname();
        userInGroup_ = group.contains(user);
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public boolean isUserInGroup()
    {
        return userInGroup_;
    }
}
