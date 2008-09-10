package org.seasar.kvasir.webapp.extension;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.webapp.handler.ExceptionHandler;
import org.seasar.kvasir.webapp.handler.impl.FilteredExceptionHandler;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean("exception-handler")
@Component(bindingType = BindingType.MUST, isa = ExceptionHandler.class)
public class ExceptionHandlerElement extends AbstractPhasedElement
{
    private String typeName_;


    public String getTypeName()
    {
        return typeName_;
    }


    @Attribute("type")
    public void setTypeName(String typeName)
    {
        typeName_ = typeName;
    }


    public Class<?> getType()
    {
        return ClassUtils.forName(typeName_, true, getParent().getParent()
            .getPlugin().getInnerClassLoader());
    }


    public ExceptionHandler getExceptionHandler()
    {
        return new FilteredExceptionHandler((ExceptionHandler)getComponent(),
            getType());
    }
}
