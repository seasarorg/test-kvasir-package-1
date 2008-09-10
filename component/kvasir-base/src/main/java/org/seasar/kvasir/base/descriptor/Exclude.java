package org.seasar.kvasir.base.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;


public class Exclude
{
    private String name_;


    public String getName()
    {
        return name_;
    }


    @Attribute
    @Required
    public void setName(String name)
    {
        name_ = name;
    }
}
