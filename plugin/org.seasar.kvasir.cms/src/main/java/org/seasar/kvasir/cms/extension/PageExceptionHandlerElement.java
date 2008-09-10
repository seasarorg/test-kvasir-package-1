package org.seasar.kvasir.cms.extension;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.impl.FilteredPageExceptionHandler;
import org.seasar.kvasir.util.ClassUtils;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = PageExceptionHandler.class)
@Bean("page-exception-handler")
public class PageExceptionHandlerElement extends AbstractPageElement
{
    private String typeName_;


    public Class<?> getType()
    {
        return ClassUtils.forName(typeName_, true, getParent().getParent()
            .getPlugin().getInnerClassLoader());
    }


    public String getTypeName()
    {
        return typeName_;
    }


    @Attribute("type")
    public void setTypeName(String typeName)
    {
        typeName_ = typeName;
    }


    public PageExceptionHandler getPageExceptionHandler()
    {
        return new FilteredPageExceptionHandler(
            (PageExceptionHandler)getComponent(), getWhat(), getHow(),
            getExcept(), isNot(), isRegex(), getGardIdProvider(), getType());
    }
}
