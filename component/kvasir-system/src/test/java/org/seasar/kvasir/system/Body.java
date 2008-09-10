package org.seasar.kvasir.system;

import net.skirnir.xom.annotation.Content;


public class Body
{
    private String content_;


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
            + ((content_ == null) ? 0 : content_.hashCode());
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
        final Body other = (Body)obj;
        if (content_ == null) {
            if (other.content_ != null)
                return false;
        } else if (!content_.equals(other.content_))
            return false;
        return true;
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
