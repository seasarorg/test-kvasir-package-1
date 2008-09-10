package org.seasar.kvasir.base.xom;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;


public class Hoe
{
    private String attribute_;

    private String hehe_;

    private Fuga fuga_;


    public String getAttribute()
    {
        return attribute_;
    }


    @Attribute
    public void setAttribute(String attribute)
    {
        attribute_ = attribute;
    }


    public String getHehe()
    {
        return hehe_;
    }


    @Child
    public void setHehe(String hehe)
    {
        hehe_ = hehe;
    }


    public Fuga getFuga()
    {
        return fuga_;
    }


    @Child
    public void setFuga(Fuga fuga)
    {
        fuga_ = fuga;
    }
}
