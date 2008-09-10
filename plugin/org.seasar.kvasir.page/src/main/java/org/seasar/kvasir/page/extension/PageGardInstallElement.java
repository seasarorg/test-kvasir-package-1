package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.gard.PageGardInstall;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.NONE)
@Bean("page-gard-install")
public class PageGardInstallElement extends AbstractElement
    implements PageGardInstall
{
    private String  gardId_;

    private String  pathname_;

    private boolean reset_ = false;


    public String getGardId()
    {
        return gardId_;
    }


    @Attribute
    @Required
    public void setGardId(String pageGardId)
    {
        gardId_ = pageGardId;
    }


    public String getPathname()
    {
        return pathname_;
    }


    @Attribute
    public void setPathname(String pathname)
    {
        pathname_ = pathname;
    }


    public boolean isReset()
    {
        return reset_;
    }


    @Attribute
    public void setReset(boolean reset)
    {
        reset_ = reset;
    }
}
