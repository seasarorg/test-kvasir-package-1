package org.seasar.kvasir.page.ability.content.impl;

import java.util.Locale;

import org.seasar.kvasir.base.cache.IndexedCacheKey;


public class ContentKey
    implements IndexedCacheKey<Integer>
{
    private Integer pageId_;

    private Locale locale_;


    public ContentKey(Integer pageId, Locale locale)
    {
        pageId_ = pageId;
        locale_ = locale;
    }


    @Override
    public String toString()
    {
        return "{pageId=" + pageId_ + ", locale=" + locale_ + "}";
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((locale_ == null) ? 0 : locale_.hashCode());
        result = PRIME * result + ((pageId_ == null) ? 0 : pageId_.hashCode());
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
        final ContentKey other = (ContentKey)obj;
        if (locale_ == null) {
            if (other.locale_ != null)
                return false;
        } else if (!locale_.equals(other.locale_))
            return false;
        if (pageId_ == null) {
            if (other.pageId_ != null)
                return false;
        } else if (!pageId_.equals(other.pageId_))
            return false;
        return true;
    }


    public Locale getLocale()
    {
        return locale_;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public Integer getIndex()
    {
        return pageId_;
    }
}
