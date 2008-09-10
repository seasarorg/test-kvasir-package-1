package org.seasar.kvasir.cms.extension;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.impl.FilteredPageProcessor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = PageProcessor.class)
@Bean("page-processor")
public class PageProcessorElement extends AbstractPageElement
{
    public static final String METHOD_ALL = "*";

    public static final String[] METHODS_DEFAULT = new String[] { "GET", "POST" };

    private String method_;


    public String[] getMethods()
    {
        if (method_ == null) {
            return METHODS_DEFAULT;
        } else {
            return method_.split(",");
        }
    }


    public String getMethod()
    {
        return method_;
    }


    @Attribute
    public void setMethod(String method)
    {
        method_ = method;
    }


    public PageProcessor getPageProcessor()
    {
        String[] methods;
        if (METHOD_ALL.equals(method_)) {
            methods = null;
        } else {
            methods = getMethods();
        }
        return new FilteredPageProcessor((PageProcessor)getComponent(),
            getWhat(), getHow(), getExcept(), isNot(), isRegex(),
            getGardIdProvider(), methods);
    }
}
