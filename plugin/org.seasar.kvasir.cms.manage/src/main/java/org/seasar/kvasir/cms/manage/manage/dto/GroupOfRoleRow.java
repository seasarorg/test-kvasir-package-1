package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.Group;


public class GroupOfRoleRow
{
    private int id_;

    private String pathname_;

    private boolean inRole_;


    public GroupOfRoleRow(Role role, Group group)
    {
        id_ = group.getId();
        pathname_ = (role.getHeimId() == group.getHeimId()) ? group
            .getPathname() : "/" + group.getPathname();
        inRole_ = role.contains(group);
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
