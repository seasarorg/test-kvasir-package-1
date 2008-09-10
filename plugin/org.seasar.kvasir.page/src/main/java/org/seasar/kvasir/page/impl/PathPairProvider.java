package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.impl.AbstractObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;


public class PathPairProvider extends
    AbstractObjectProvider<PathPairKey, String>
    implements ObjectProvider<PathPairKey, String>
{
    private PageAlfr pageAlfr_;


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public CachedEntry<PathPairKey, String> get(PathPairKey key)
    {
        Page page = pageAlfr_.findPage(key.getHeimId(), key
            .getBasePathname(), key.getSubPathname());

        return newEntry(key, page != null ? page.getPathname() : null);
    }


    public boolean isModified(CachedEntry<PathPairKey, String> entry)
    {
        return false;
    }


    public CachedEntry<PathPairKey, String> newEntry(PathPairKey key,
        String object)
    {
        return new CachedEntryImpl<PathPairKey, String>(key, 1, object);
    }
}
