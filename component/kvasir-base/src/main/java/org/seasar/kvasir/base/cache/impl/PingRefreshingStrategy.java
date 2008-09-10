package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.RefreshingStrategy;


public class PingRefreshingStrategy<K, T>
    implements RefreshingStrategy<K, T>
{
    public boolean shouldRefreshByPing(CachedEntry<K, T> entry)
    {
        return true;
    }


    public boolean shouldRefreshByUse(CachedEntry<K, T> entry)
    {
        return false;
    }
}
