package org.seasar.kvasir.base.plugin.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


public class Export
{
    public static final String NAME_ALL = "**";

    private Library parent_;

    private String name_;

    private boolean resource_;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<export");
        if (name_ != null) {
            sb.append(" name=\"").append(name_).append("\"");
        }
        if (resource_ != false) {
            sb.append(" resource=\"").append(resource_).append("\"");
        }
        sb.append(" />");
        return sb.toString();
    }


    public Library getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Library parent)
    {
        parent_ = parent;
    }


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


    public boolean isResource()
    {
        return resource_;
    }


    @Attribute
    @Default("false")
    public void setResource(boolean resource)
    {
        resource_ = resource;
    }

}
