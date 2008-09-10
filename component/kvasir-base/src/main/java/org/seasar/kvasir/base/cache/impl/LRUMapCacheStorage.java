package org.seasar.kvasir.base.cache.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.seasar.kvasir.base.cache.CachedEntry;


public class LRUMapCacheStorage<K, T> extends AbstractCacheStorage<K, T>
{
    private LRUMap<K, CachedEntry<K, T>> map_;

    private int maxSize_;


    public LRUMapCacheStorage()
    {
        this(100);
    }


    public LRUMapCacheStorage(int maxSize)
    {
        map_ = new LRUMap<K, CachedEntry<K, T>>(maxSize);
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


    private static class LRUMap<K, V> extends LinkedHashMap<K, V>
    {
        private static final long serialVersionUID = -2303676717655195753L;

        private static final int DEFAULT_INITIALCAPACITY = 16;

        private static final float DEFAULT_LOADFACTOR = 0.75f;

        private int maxSize_;


        public LRUMap(int maxSize)
        {
            this(maxSize, DEFAULT_INITIALCAPACITY, DEFAULT_LOADFACTOR);
        }


        public LRUMap(int maxSize, int initialCapacity)
        {
            this(maxSize, initialCapacity, 0.75f);
        }


        public LRUMap(int maxSize, int initialCapacity, float loadFactor)
        {
            super(initialCapacity, loadFactor, true);
            maxSize_ = maxSize;
        }


        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest)
        {
            return size() > maxSize_;
        }
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
