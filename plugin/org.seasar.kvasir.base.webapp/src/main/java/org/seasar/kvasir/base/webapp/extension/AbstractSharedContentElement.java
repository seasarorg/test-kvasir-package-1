package org.seasar.kvasir.base.webapp.extension;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;


abstract public class AbstractSharedContentElement extends
    AbstractContentElement
{
    private String version_;


    @Override
    @Attribute
    @Required
    public void setId(String id)
    {
        super.setId(id);
    }


    public String getVersion()
    {
        return version_;
    }


    @Attribute
    public void setVersion(String version)
    {
        version_ = version;
    }


    protected String getPath()
    {
        return getBasePath() + "/" + getId();
    }


    abstract protected String getBasePath();
}
