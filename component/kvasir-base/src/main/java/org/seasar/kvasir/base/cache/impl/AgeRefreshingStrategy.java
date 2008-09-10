package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.RefreshingStrategy;


public class AgeRefreshingStrategy<K, T>
    implements RefreshingStrategy<K, T>
{
    private int maxAge_;


    public AgeRefreshingStrategy()
    {
        this(20);
    }


    public AgeRefreshingStrategy(int maxAge)
    {
        maxAge_ = maxAge;
    }


    public boolean shouldRefreshByPing(CachedEntry<K, T> entry)
    {
        return false;
    }


    public boolean shouldRefreshByUse(CachedEntry<K, T> entry)
    {
        return (entry.getAge() > maxAge_);
    }
}
