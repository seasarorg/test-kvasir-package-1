package org.seasar.kvasir.base.webapp.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;


abstract public class AbstractUseSharedContentElement extends AbstractElement
{
    private String moduleName_;


    @Override
    @Required
    public void setId(String id)
    {
        super.setId(id);
    }


    public String getModuleName()
    {
        if (moduleName_ != null) {
            return moduleName_;
        } else {
            return getId();
        }
    }


    public String getModulePathname()
    {
        return getBasePath() + "/" + getId() + "/" + getModuleName();
    }


    abstract protected String getBasePath();


    @Attribute
    @Required
    public void setModuleName(String resourcePath)
    {
        moduleName_ = resourcePath;
    }
}
