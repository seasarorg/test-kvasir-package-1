package org.seasar.kvasir.page.ability.template.impl;

import org.seasar.kvasir.base.cache.IndexedCacheKey;


public class TemplateKey
    implements IndexedCacheKey<Integer>
{
    private Integer pageId_;

    private String variant_;


    public TemplateKey(Integer pageId, String variant)
    {
        pageId_ = pageId;
        variant_ = variant;
    }


    @Override
    public String toString()
    {
        return "{pageId=" + pageId_ + ", variant=" + variant_ + "}";
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((pageId_ == null) ? 0 : pageId_.hashCode());
        result = PRIME * result
            + ((variant_ == null) ? 0 : variant_.hashCode());
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
        final TemplateKey other = (TemplateKey)obj;
        if (pageId_ == null) {
            if (other.pageId_ != null)
                return false;
        } else if (!pageId_.equals(other.pageId_))
            return false;
        if (variant_ == null) {
            if (other.variant_ != null)
                return false;
        } else if (!variant_.equals(other.variant_))
            return false;
        return true;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public String getVariant()
    {
        return variant_;
    }


    public Integer getIndex()
    {
        return pageId_;
    }
}
