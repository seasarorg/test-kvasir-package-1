package org.seasar.kvasir.system;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;


public class Structure
{
    private String name_;

    private Body body_;


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((body_ == null) ? 0 : body_.hashCode());
        result = PRIME * result + ((name_ == null) ? 0 : name_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Structure other = (Structure)obj;
        if (body_ == null) {
            if (other.body_ != null)
                return false;
        } else if (!body_.equals(other.body_))
            return false;
        if (name_ == null) {
            if (other.name_ != null)
                return false;
        } else if (!name_.equals(other.name_))
            return false;
        return true;
    }


    public String getName()
    {
        return name_;
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public Body getBody()
    {
        return body_;
    }


    @Child
    public void setBody(Body body)
    {
        body_ = body;
    }
}
