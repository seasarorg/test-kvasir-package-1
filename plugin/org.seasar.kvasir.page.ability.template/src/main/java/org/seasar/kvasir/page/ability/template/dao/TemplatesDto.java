package org.seasar.kvasir.page.ability.template.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "templates")
public class TemplatesDto
{
    private Integer pageId_;

    private String variant_;

    private String body_;

    private Date modifyDate_;


    public TemplatesDto()
    {
    }


    public TemplatesDto(Integer pageId, String variant, String body,
        Date modifyDate)
    {
        pageId_ = pageId;
        variant_ = variant;
        body_ = body;
        modifyDate_ = modifyDate;
    }


    public String getBody()
    {
        return body_;
    }


    public void setBody(String body)
    {
        body_ = body;
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


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }
}
