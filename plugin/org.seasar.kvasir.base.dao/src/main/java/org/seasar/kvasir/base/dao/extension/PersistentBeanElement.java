package org.seasar.kvasir.base.dao.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean("persistent-bean")
public class PersistentBeanElement extends AbstractElement
{
    private String className_;


    public String getClassName()
    {
        return className_;
    }


    @Attribute("class")
    public void setClassName(String className)
    {
        className_ = className;
    }
}
