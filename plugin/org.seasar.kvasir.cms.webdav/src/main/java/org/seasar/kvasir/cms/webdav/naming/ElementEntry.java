package org.seasar.kvasir.cms.webdav.naming;

public class ElementEntry
{
    private String name_;

    private boolean isCollection_;


    public ElementEntry(String name, boolean isCollection)
    {
        name_ = name;
        isCollection_ = isCollection;
    }


    public String getName()
    {
        return name_;
    }


    public boolean isCollection()
    {
        return isCollection_;
    }
}
