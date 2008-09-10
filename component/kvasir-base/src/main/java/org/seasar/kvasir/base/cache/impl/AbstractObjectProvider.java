package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;


abstract public class AbstractObjectProvider<K, T>
    implements ObjectProvider<K, T>
{
    public CachedEntry<K, T> refresh(CachedEntry<K, T> entry)
    {
        if (isModified(entry)) {
            return get(entry.getKey());
        } else {
            return entry;
        }
    }
}
