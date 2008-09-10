package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "casto")
public class CastDto
{
    private Integer roleId_;

    private Integer groupId_;

    private Integer userId_;


    public Integer getRoleId()
    {
        return roleId_;
    }


    public void setRoleId(Integer roleId)
    {
        roleId_ = roleId;
    }


    public Integer getGroupId()
    {
        return groupId_;
    }


    public void setGroupId(Integer groupId)
    {
        groupId_ = groupId;
    }


    public Integer getUserId()
    {
        return userId_;
    }


    public void setUserId(Integer userId)
    {
        userId_ = userId;
    }
}
