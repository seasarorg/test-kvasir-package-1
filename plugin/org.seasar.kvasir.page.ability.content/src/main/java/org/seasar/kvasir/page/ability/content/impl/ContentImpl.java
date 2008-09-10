package org.seasar.kvasir.page.ability.content.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.page.ability.content.dao.ContentsDto;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


public class ContentImpl
    implements Content
{
    private ContentsDto dto_;

    private ContentAbilityPlugin plugin_;

    private Object compiledBody_;


    public ContentImpl(ContentsDto dto, ContentAbilityPlugin plugin)
    {
        dto_ = dto;
        plugin_ = plugin;
    }


    public int getId()
    {
        return dto_.getId();
    }


    public byte[] getBodyBytes()
    {
        return IOUtils.readBytes(getBodyInputStream());
    }


    public InputStream getBodyInputStream()
    {
        try {
            return getBodyResource().getInputStream();
        } catch (ResourceNotFoundException ex) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }


    public String getBodyString()
    {
        return IOUtils.readString(getBodyInputStream(), getEncoding(), false);
    }


    public Date getCreateDate()
    {
        return dto_.getCreateDate();
    }


    public String getEncoding()
    {
        return dto_.getEncoding();
    }


    public String getMediaType()
    {
        return dto_.getMediaType();
    }


    public Date getModifyDate()
    {
        return dto_.getModifyDate();
    }


    public Resource getBodyResource()
    {
        return plugin_.getContentResource(dto_.getId());
    }


    public int getRevisionNumber()
    {
        return dto_.getRevisionNumber();
    }


    public String getVariant()
    {
        return dto_.getVariant();
    }


    public String getBodyHTMLString(VariableResolver resolver)
    {
        ContentHandler handler = plugin_.getContentHandler(getMediaType());
        if (compiledBody_ == null) {
            compiledBody_ = handler.compile(getBodyInputStream(),
                getEncoding(), getMediaType());
        }
        return handler.toHTML(compiledBody_, resolver);
    }
}
