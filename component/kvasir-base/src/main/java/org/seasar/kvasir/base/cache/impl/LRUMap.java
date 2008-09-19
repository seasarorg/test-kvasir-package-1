package org.seasar.kvasir.base.cache.impl;

import java.util.LinkedHashMap;
import java.util.Map.Entry;


class LRUMap<K, V> extends LinkedHashMap<K, V>
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
