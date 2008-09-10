package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CachePlugin;
import org.seasar.kvasir.base.cache.ManagedCache;
import org.seasar.kvasir.base.cache.RefreshingStrategy;


/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public abstract class AbstractCachedPageAbilityAlfr extends
    AbstractPageAbilityAlfr
    implements ManagedCache
{
    private CachePlugin cachePlugin_;


    public void setCachePlugin(CachePlugin cachePlugin)
    {
        cachePlugin_ = cachePlugin;
    }


    @Override
    @SuppressWarnings("unchecked")
    protected boolean doStart()
    {
        RefreshingStrategy refreshingStrategy = cachePlugin_
            .newRefreshingStrategyForVolatileProvider(getCache());
        getCache().setRefreshingStrategy(refreshingStrategy);
        cachePlugin_.register(getCacheId(), this);
        return true;
    }


    abstract protected Cache<?, ?> getCache();


    abstract protected String getCacheId();


    public synchronized long getTotalSize()
    {
        return getCache().getTotalSize();
    }


    public synchronized long getUsedSize()
    {
        return getCache().getUsedSize();
    }


    public synchronized void ping()
    {
        getCache().ping();
    }


    public synchronized void refresh()
    {
        getCache().refresh();
    }
}
