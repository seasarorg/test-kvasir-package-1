package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.base.cache.IndexedCacheKey;


public class PathPairKey
    implements IndexedCacheKey<Integer>
{
    private Integer heimId_;

    private String basePathname_;

    private String subPathname_;


    PathPairKey(Integer heimId, String basePathname, String subPathname)
    {
        heimId_ = heimId;
        basePathname_ = basePathname;
        subPathname_ = subPathname;
    }


    @Override
    public String toString()
    {
        return heimId_ + ":" + basePathname_ + ":" + subPathname_;
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
            + ((basePathname_ == null) ? 0 : basePathname_.hashCode());
        result = PRIME * result + ((heimId_ == null) ? 0 : heimId_.hashCode());
        result = PRIME * result
            + ((subPathname_ == null) ? 0 : subPathname_.hashCode());
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
        final PathPairKey other = (PathPairKey)obj;
        if (basePathname_ == null) {
            if (other.basePathname_ != null)
                return false;
        } else if (!basePathname_.equals(other.basePathname_))
            return false;
        if (heimId_ == null) {
            if (other.heimId_ != null)
                return false;
        } else if (!heimId_.equals(other.heimId_))
            return false;
        if (subPathname_ == null) {
            if (other.subPathname_ != null)
                return false;
        } else if (!subPathname_.equals(other.subPathname_))
            return false;
        return true;
    }


    public String getBasePathname()
    {
        return basePathname_;
    }


    public Integer getHeimId()
    {
        return heimId_;
    }


    public String getSubPathname()
    {
        return subPathname_;
    }


    public Integer getIndex()
    {
        return heimId_;
    }
}
