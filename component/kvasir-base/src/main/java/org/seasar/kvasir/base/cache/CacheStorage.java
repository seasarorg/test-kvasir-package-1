package org.seasar.kvasir.base.cache;

import java.util.Iterator;


/**
 * オブジェクトをキャッシュする保存領域を表すインタフェースです。
 * <p>このインタフェースを用いて、キーに関連付けてオブジェクトをキャッシュしたりオブジェクトを取り出したりすることができます。
 * </p>
 * <p>このインタフェースは通常{@link Cache}インタフェースとともに使用されます。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @see Cache
 * @author YOKOTA Takehiko
 */
public interface CacheStorage<K, T>
{
    int TOTALSIZE_UNLIMITED = -1;


    CachedEntry<K, T> get(K key);


    void register(CachedEntry<K, T> entry);


    void remove(K key);


    void clear();


    Iterator<CachedEntry<K, T>> getEntryIterator();


    /**
     * CacheListenerを追加します。
     * <p>このメソッドはキャッシュを利用し始める前に呼び出すようにして下さい。
     * </p>
     *
     * @param listener リスナ。
     */
    void addListener(CacheListener<K, T> listener);


    long getTotalSize();


    long getUsedSize();
}
