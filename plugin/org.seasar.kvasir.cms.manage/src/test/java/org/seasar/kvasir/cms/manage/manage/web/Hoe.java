package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.List;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;


public class Hoe
{
    private String attribute_;

    private String child_;

    private List<Fuga> fugaList_ = new ArrayList<Fuga>();

    private Hoho hoho_;

    private List<String> stringList_ = new ArrayList<String>();


    public String getAttribute()
    {
        return attribute_;
    }


    @Attribute
    public void setAttribute(String attribute)
    {
        attribute_ = attribute;
    }


    public String getChild()
    {
        return child_;
    }


    @Child
    public void setChild(String child)
    {
        child_ = child;
    }


    public Fuga[] getFugas()
    {
        return fugaList_.toArray(new Fuga[0]);
    }


    @Child
    public void addFuga(Fuga fuga)
    {
        fugaList_.add(fuga);
    }


    public Hoho getHoho()
    {
        return hoho_;
    }


    @Child
    public void setHoho(Hoho hoho)
    {
        hoho_ = hoho;
    }


    public String[] getStrings()
    {
        return stringList_.toArray(new String[0]);
    }


    @Child
    public void addString(String string)
    {
        stringList_.add(string);
    }
}
