package org.seasar.kvasir.base.cache;

public interface IndexedCache<I, K extends IndexedCacheKey<I>, T>
    extends Cache<K, T>
{
    void clear(I index);
}
