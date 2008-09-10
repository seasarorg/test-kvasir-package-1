package org.seasar.kvasir.cms.manage.manage.dto;

import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.Group;


public class RoleOfGroupRow
{
    private int id_;

    private String pathname_;

    private boolean groupInRole_;


    public RoleOfGroupRow(Group group, Role role)
    {
        id_ = role.getId();
        pathname_ = (group.getHeimId() == role.getHeimId()) ? role
            .getPathname() : "/" + role.getPathname();
        groupInRole_ = role.contains(group);
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public boolean isGroupInRole()
    {
        return groupInRole_;
    }
}
