package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.descriptor.AbstractElement;

import net.skirnir.xom.annotation.Attribute;


/**
 * @author YOKOTA Takehiko
 */
public class Element4 extends AbstractElement
{
    private String child_;


    public String getChild()
    {
        return child_;
    }


    @Attribute
    public void setChild(String child)
    {
        child_ = child;
    }
}
