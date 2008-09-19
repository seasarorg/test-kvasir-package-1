package org.seasar.kvasir.base.cache.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skirnir.xom.annotation.Child;


public class CachePluginSettings
{
    private static final String DEFAULT_ID = null;

    private static final int DEFAULT_MAXAGE = 20;

    private static final int DEFAULT_SIZE = 500;

    private List<CacheElement> cacheList_ = new ArrayList<CacheElement>();

    private Map<String, CacheElement> cacheMap_ = new HashMap<String, CacheElement>();


    public CacheElement[] getCaches()
    {
        return cacheList_.toArray(new CacheElement[0]);
    }


    @Child
    public void addCache(CacheElement cache)
    {
        cacheList_.add(cache);
        cacheMap_.put(cache.getId(), cache);
    }


    public int getMaxAge(String id)
    {
        CacheElement cache = cacheMap_.get(id);
        if (cache != null) {
            Integer maxAge = cache.getMaxAge();
            if (maxAge != null) {
                return maxAge.intValue();
            }
        }
        cache = cacheMap_.get(DEFAULT_ID);
        if (cache != null) {
            Integer maxAge = cache.getMaxAge();
            if (maxAge != null) {
                return maxAge.intValue();
            }
        }
        return DEFAULT_MAXAGE;
    }


    public int getMaxSize(String id)
    {
        CacheElement cache = cacheMap_.get(id);
        if (cache != null) {
            Integer maxSize = cache.getMaxSize();
            if (maxSize != null) {
                return maxSize.intValue();
            }
        }
        cache = cacheMap_.get(DEFAULT_ID);
        if (cache != null) {
            Integer maxSize = cache.getMaxSize();
            if (maxSize != null) {
                return maxSize.intValue();
            }
        }
        return DEFAULT_SIZE;
    }
}
