package org.seasar.kvasir.page.ability.content.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "content")
public class ContentDto
{
    private Integer pageId_;

    private Date modifyDate_;


    public ContentDto()
    {
    }


    public ContentDto(Integer pageId, Date modifyDate)
    {
        pageId_ = pageId;
        modifyDate_ = modifyDate;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer pageId)
    {
        pageId_ = pageId;
    }
}
