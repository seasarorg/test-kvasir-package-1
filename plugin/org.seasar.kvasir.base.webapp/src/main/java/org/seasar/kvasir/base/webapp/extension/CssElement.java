package org.seasar.kvasir.base.webapp.extension;

import static org.seasar.kvasir.base.webapp.WebappPlugin.PATH_CSS;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Bean;


@Bean("css")
@Component(bindingType = BindingType.NONE)
public class CssElement extends AbstractSharedContentElement
{
    private static final String CONTENTTYPE = "text/css";


    @Override
    protected String getBasePath()
    {
        return PATH_CSS;
    }


    @Override
    protected String getDefaultContentType()
    {
        return CONTENTTYPE;
    }
}
