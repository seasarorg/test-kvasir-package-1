package org.seasar.kvasir.page.ability.template.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.dao.TemplatesDto;
import org.seasar.kvasir.util.io.Resource;


public class TemplateImpl
    implements Template
{
    private String body_;

    private Date modifyDate_;

    private Long size_;

    private Resource bodyResource_;


    public TemplateImpl(TemplatesDto dto)
    {
        body_ = dto.getBody();
        modifyDate_ = dto.getModifyDate();
    }


    public String getBody()
    {
        return body_;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public long getSize()
    {
        if (size_ == null) {
            try {
                size_ = Long.valueOf(body_.getBytes(ENCODING).length);
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
        }
        return size_.longValue();
    }


    public Resource getBodyResource()
    {
        return bodyResource_;
    }


    public void setBodyResource(Resource bodyResource)
    {
        bodyResource_ = bodyResource;
    }
}
