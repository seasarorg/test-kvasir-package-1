package org.seasar.kvasir.cms.extension;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean("phase")
public class PageFilterPhaseElement extends AbstractPageElement
{
    private boolean default_;


    public boolean isDefault()
    {
        return default_;
    }


    @Attribute
    public void setDefault(boolean defaults)
    {
        default_ = defaults;
    }
}
