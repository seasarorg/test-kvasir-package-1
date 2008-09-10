package org.seasar.kvasir.base.cache;

public interface IndexedCacheStorage<I, K extends IndexedCacheKey<I>, T>
    extends CacheStorage<K, T>
{
    void clear(I index);
}
