package org.seasar.kvasir.base.cache;

/**
 * オブジェクトをキャッシュする機構を表すインタフェースです。
 * <p>このインタフェースを用いてキーに関連付けられたオブジェクトを取得することができます。
 * その際オブジェクトは内部で適切にキャッシュされます。
 * </p>
 * <p>オブジェクトをキャッシュしている実体は{@link CacheStorage}です。
 * オブジェクトがキャッシュにない場合はオブジェクトを生成して返しますが、
 * オブジェクトの生成を司るのが{@link ObjectProvider}です。
 * キャッシュのリフレッシュタイミングは{@link RefreshingStrategy}によって制御されます。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @see ObjectProvider
 * @see RefreshingStrategy
 * @see CacheStorage
 * @author YOKOTA Takehiko
 */
public interface Cache<K, T>
    extends ManagedCache
{
    CachedEntry<K, T> getEntry(K key);


    CachedEntry<K, T> getEntry(K key, boolean registerIfNotExists);


    T get(K key);


    void ping();


    void ping(K key);


    void refresh();


    void refresh(K key);


    void remove(K key);


    void clear();


    void register(K key, T object);


    ObjectProvider<K, T> getObjectProvider();


    Cache<K, T> setObjectProvider(ObjectProvider<K, T> objectProvider);


    CacheStorage<K, T> getCacheStorage();


    Cache<K, T> setCacheStorage(CacheStorage<K, T> cacheStorage);


    RefreshingStrategy<K, T> getRefreshingStrategy();


    Cache<K, T> setRefreshingStrategy(
        RefreshingStrategy<K, T> refreshingStrategy);


    /**
     * CacheListenerを追加します。
     * <p>このメソッドはキャッシュを利用し始めた後は呼び出さないで下さい。
     * </p>
     *
     * @param listener リスナ。
     */
    void addListener(CacheListener<K, T> listener);
}
