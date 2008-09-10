package org.seasar.kvasir.base.webapp.extension;

import static org.seasar.kvasir.base.webapp.WebappPlugin.PATH_JS;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Default;


@Bean("use-javascript")
@Component(bindingType = BindingType.NONE)
public class UseJavascriptElement extends AbstractUseSharedContentElement
{
    private String basePath_;


    @Override
    public String getBasePath()
    {
        if (basePath_ == null) {
            return PATH_JS;
        } else {
            return basePath_;
        }
    }


    @Attribute
    @Default(PATH_JS)
    public void setBasePath(String basePath)
    {
        basePath_ = basePath;
    }
}
