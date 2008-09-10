package org.seasar.kvasir.system.container.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Content;


public class Property
{
    private String name_;

    private String content_;


    public String getName()
    {
        return name_;
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public String getContent()
    {
        return content_;
    }


    @Content
    public void setContent(String content)
    {
        content_ = content;
    }
}
