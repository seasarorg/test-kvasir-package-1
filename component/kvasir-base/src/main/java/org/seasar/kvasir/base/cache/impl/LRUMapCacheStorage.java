package org.seasar.kvasir.base.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.seasar.kvasir.base.cache.CachedEntry;


public class LRUMapCacheStorage<K, T> extends AbstractCacheStorage<K, T>
{
    private Map<K, CachedEntry<K, T>> map_;

    private int maxSize_;


    public LRUMapCacheStorage()
    {
        this(500);
    }


    public LRUMapCacheStorage(int maxSize)
    {
        if (maxSize < 0) {
            // ConcurrentHashMapはnullを指定できないので…。残念。
            map_ = Collections
                .synchronizedMap(new HashMap<K, CachedEntry<K, T>>());
        } else {
            map_ = Collections
                .synchronizedMap(new LRUMap<K, CachedEntry<K, T>>(maxSize));
        }
        maxSize_ = maxSize;
    }


    public void clear()
    {
        map_.clear();
    }


    public CachedEntry<K, T> get(K key)
    {
        return map_.get(key);
    }


    public Iterator<CachedEntry<K, T>> getEntryIterator()
    {
        return map_.values().iterator();
    }


    public void register(CachedEntry<K, T> entry)
    {
        map_.put(entry.getKey(), entry);

        notifyRegistered(entry);
    }


    public void remove(K key)
    {
        map_.remove(key);
    }


    public long getTotalSize()
    {
        return maxSize_;
    }


    public long getUsedSize()
    {
        return map_.size();
    }
}
