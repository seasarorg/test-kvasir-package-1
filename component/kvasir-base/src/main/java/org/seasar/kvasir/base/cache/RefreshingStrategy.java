package org.seasar.kvasir.base.cache;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface RefreshingStrategy<K, T>
{
    boolean shouldRefreshByPing(CachedEntry<K, T> entry);


    boolean shouldRefreshByUse(CachedEntry<K, T> entry);
}
