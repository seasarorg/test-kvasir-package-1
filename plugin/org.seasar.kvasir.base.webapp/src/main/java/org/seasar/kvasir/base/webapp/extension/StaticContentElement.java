package org.seasar.kvasir.base.webapp.extension;

import org.seasar.kvasir.base.mime.MimeMappings;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Bean("static-content")
public class StaticContentElement extends AbstractContentElement
{
    private static final String CONTENTTYPE_DEFAULT = "application/octet-stream";

    private String contentType_;

    private String path_;


    public String getContentType()
    {
        return contentType_;
    }


    @Attribute
    public void setContentType(String contentType)
    {
        contentType_ = contentType;
    }


    public String getPath()
    {
        return path_;
    }


    @Attribute
    @Required
    public void setPath(String path)
    {
        path_ = path;
    }


    @Override
    protected String getContentType(String path, MimeMappings mappings)
    {
        if (contentType_ != null) {
            return contentType_;
        } else {
            return super.getContentType(path, mappings);
        }
    }


    @Override
    protected String getDefaultContentType()
    {
        return CONTENTTYPE_DEFAULT;
    }
}
