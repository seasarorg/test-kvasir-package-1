package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.RefreshingStrategy;


public class TimeRefreshingStrategy<K, T>
    implements RefreshingStrategy<K, T>
{
    private long maxTimeMillis_;


    public TimeRefreshingStrategy()
    {
        this(1000L * 60);
    }


    public TimeRefreshingStrategy(long maxTimeMillis)
    {
        maxTimeMillis_ = maxTimeMillis;
    }


    public boolean shouldRefreshByPing(CachedEntry<K, T> entry)
    {
        return shouldRefresh(entry);
    }


    public boolean shouldRefreshByUse(CachedEntry<K, T> entry)
    {
        return shouldRefresh(entry);
    }


    boolean shouldRefresh(CachedEntry<K, T> entry)
    {
        return (System.currentTimeMillis() - entry.getCreatedTime() > maxTimeMillis_);
    }
}
