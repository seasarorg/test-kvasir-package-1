package org.seasar.kvasir.base.webapp.extension;

import static org.seasar.kvasir.base.webapp.WebappPlugin.PATH_CSS;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Default;


@Bean("use-css")
@Component(bindingType = BindingType.NONE)
public class UseCssElement extends AbstractUseSharedContentElement
{
    private String basePath_;


    @Override
    public String getBasePath()
    {
        if (basePath_ == null) {
            return PATH_CSS;
        } else {
            return basePath_;
        }
    }


    @Attribute
    @Default(PATH_CSS)
    public void setBasePath(String basePath)
    {
        basePath_ = basePath;
    }
}
