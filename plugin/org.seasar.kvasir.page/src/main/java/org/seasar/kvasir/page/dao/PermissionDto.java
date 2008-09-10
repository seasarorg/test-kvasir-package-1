package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "permission")
public class PermissionDto
{
    private Integer pageId_;

    private Integer roleId_;

    private Integer type_;

    private Integer level_;


    public Integer getRoleId()
    {
        return roleId_;
    }


    public void setRoleId(Integer roleId)
    {
        roleId_ = roleId;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer groupId)
    {
        pageId_ = groupId;
    }


    public Integer getType()
    {
        return type_;
    }


    public void setType(Integer userId)
    {
        type_ = userId;
    }


    public Integer getLevel()
    {
        return level_;
    }


    public void setLevel(Integer level)
    {
        level_ = level;
    }
}
