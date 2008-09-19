package org.seasar.kvasir.base.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.IndexedCacheKey;
import org.seasar.kvasir.base.cache.IndexedCacheStorage;
import org.seasar.kvasir.base.util.ArrayUtils;


public class IndexedCacheStorageImpl<I, K extends IndexedCacheKey<I>, T>
    implements IndexedCacheStorage<I, K, T>
{
    @SuppressWarnings("unchecked")
    private CacheListener<K, T>[] listeners_ = new CacheListener[0];

    private Map<I, CacheStorage<K, T>> storageMap_;


    public IndexedCacheStorageImpl()
    {
        this(500);
    }


    public IndexedCacheStorageImpl(int maxSize)
    {
        if (maxSize < 0) {
            // ConcurrentHashMapはnullを指定できないので…。残念。
            storageMap_ = Collections
                .synchronizedMap(new HashMap<I, CacheStorage<K, T>>());
        } else {
            storageMap_ = Collections
                .synchronizedMap(new LRUMap<I, CacheStorage<K, T>>(maxSize));
        }
    }


    public void clear()
    {
        storageMap_.clear();
    }


    public CachedEntry<K, T> get(K key)
    {
        CacheStorage<K, T> storage = getStorage(key.getIndex(), false);
        if (storage != null) {
            return storage.get(key);
        } else {
            return null;
        }
    }


    public Iterator<CachedEntry<K, T>> getEntryIterator()
    {
        throw new UnsupportedOperationException();
    }


    public void register(CachedEntry<K, T> entry)
    {
        getStorage(entry.getKey().getIndex(), true).register(entry);
    }


    public void remove(K key)
    {
        CacheStorage<K, T> storage = getStorage(key.getIndex(), false);
        if (storage != null) {
            storage.remove(key);
        }
    }


    CacheStorage<K, T> getStorage(I id, boolean create)
    {
        CacheStorage<K, T> storage = storageMap_.get(id);
        if (storage == null && create) {
            storage = newCacheStorage();
            for (int i = 0; i < listeners_.length; i++) {
                storage.addListener(listeners_[i]);
            }
            storageMap_.put(id, storage);
        }
        return storage;
    }


    protected CacheStorage<K, T> newCacheStorage()
    {
        return new LRUMapCacheStorage<K, T>();
    }


    public void clear(I id)
    {
        storageMap_.remove(id);
    }


    public void addListener(CacheListener<K, T> listener)
    {
        listeners_ = ArrayUtils.add(listeners_, listener);
    }


    public long getTotalSize()
    {
        long totalSize = 0;
        for (Iterator<CacheStorage<K, T>> itr = storageMap_.values().iterator(); itr
            .hasNext();) {
            long total = itr.next().getTotalSize();
            if (total == TOTALSIZE_UNLIMITED) {
                return TOTALSIZE_UNLIMITED;
            }
            totalSize += total;
        }
        return totalSize;
    }


    public long getUsedSize()
    {
        long usedSize = 0;
        for (Iterator<CacheStorage<K, T>> itr = storageMap_.values().iterator(); itr
            .hasNext();) {
            usedSize += itr.next().getTotalSize();
        }
        return usedSize;
    }
}
