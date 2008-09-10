package org.seasar.kvasir.page.ability.template.dao;

import org.seasar.dao.annotation.tiger.Bean;


@Bean(table = "template")
public class TemplateDto
{
    private Integer pageId_;

    private String type_;

    private String responseContentType_;


    public TemplateDto()
    {
    }


    public TemplateDto(Integer pageId, String type, String responseContentType)
    {
        pageId_ = pageId;
        type_ = type;
        responseContentType_ = responseContentType;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer id)
    {
        pageId_ = id;
    }


    public String getResponseContentType()
    {
        return responseContentType_;
    }


    public void setResponseContentType(String responseContentType)
    {
        responseContentType_ = responseContentType;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }
}
