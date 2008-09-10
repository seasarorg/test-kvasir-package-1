package org.seasar.kvasir.base.xom;

import net.skirnir.xom.annotation.Attribute;

public class Fuga
{
    private String attribute_;


    public String getAttribute()
    {
        return attribute_;
    }


    @Attribute
    public void setAttribute(String attribute)
    {
        attribute_ = attribute;
    }
}
