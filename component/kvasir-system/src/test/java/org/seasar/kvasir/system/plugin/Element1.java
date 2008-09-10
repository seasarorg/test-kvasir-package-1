package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.descriptor.AbstractGenericElement;

import net.skirnir.xom.annotation.Child;


/**
 * @author YOKOTA Takehiko
 */
public class Element1 extends AbstractGenericElement
    implements Interface1
{
    private String child_;


    public String getChild()
    {
        return child_;
    }


    @Child
    public void setChild(String child)
    {
        child_ = child;
    }
}
