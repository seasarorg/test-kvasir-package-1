package org.seasar.kvasir.page.impl;

public class PageKey
{
    private Number id_;

    private Number heimId_;

    private String pathname_;


    public PageKey(Number id)
    {
        id_ = id;
    }


    public PageKey(Number heimId, String pathname)
    {
        heimId_ = heimId;
        pathname_ = pathname;
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((heimId_ == null) ? 0 : heimId_.hashCode());
        result = PRIME * result + ((id_ == null) ? 0 : id_.hashCode());
        result = PRIME * result
            + ((pathname_ == null) ? 0 : pathname_.hashCode());
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
        final PageKey other = (PageKey)obj;
        if (heimId_ == null) {
            if (other.heimId_ != null)
                return false;
        } else if (other.heimId_ == null
            || heimId_.intValue() != other.heimId_.intValue()) {
            return false;
        }
        if (id_ == null) {
            if (other.id_ != null)
                return false;
        } else if (other.id_ == null || id_.intValue() != other.id_.intValue()) {
            return false;
        }
        if (pathname_ == null) {
            if (other.pathname_ != null)
                return false;
        } else if (!pathname_.equals(other.pathname_))
            return false;
        return true;
    }


    public Number getHeimId()
    {
        return heimId_;
    }


    public Number getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }
}
