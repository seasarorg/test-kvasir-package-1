package org.seasar.kvasir.base.xom;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;


public interface IHoe
{
    String getAttribute();


    @Attribute
    void setAttribute(String attribute);


    String getHehe();


    @Child
    void setHehe(String hehe);


    Fuga getFuga();


    @Child
    void setFuga(Fuga fuga);
}
