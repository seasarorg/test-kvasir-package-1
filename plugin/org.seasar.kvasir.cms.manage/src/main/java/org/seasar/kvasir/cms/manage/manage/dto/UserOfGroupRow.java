package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


public class UserOfGroupRow
{
    private int id_;

    private String pathname_;

    private boolean inGroup_;


    public UserOfGroupRow(Group group, User user)
    {
        id_ = user.getId();
        pathname_ = (group.getHeimId() == user.getHeimId()) ? user
            .getPathname() : "/" + user.getPathname();
        inGroup_ = group.contains(user);
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public boolean isInGroup()
    {
        return inGroup_;
    }
}
