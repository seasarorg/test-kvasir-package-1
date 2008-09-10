package org.seasar.kvasir.base.webapp.extension;

import static org.seasar.kvasir.base.webapp.WebappPlugin.PATH_JS;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Bean;


@Bean("javascript")
@Component(bindingType = BindingType.NONE)
public class JavascriptElement extends AbstractSharedContentElement
{
    private static final String CONTENTTYPE = "text/javascript";


    @Override
    protected String getBasePath()
    {
        return PATH_JS;
    }


    @Override
    protected String getDefaultContentType()
    {
        return CONTENTTYPE;
    }
}
