package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "member")
public class MemberDto
{
    private Integer groupId_;

    private Integer userId_;


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
