package org.seasar.kvasir.base.cache;

import org.seasar.kvasir.base.cache.setting.CachePluginSettings;
import org.seasar.kvasir.base.plugin.Plugin;


public interface CachePlugin
    extends Plugin<CachePluginSettings>
{
    String ID = "org.seasar.kvasir.base.cache";

    String ID_PATH = ID.replace('.', '/');


    /**
     * マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュのための、
     * RefreshingStrategyインスタンスを生成して返します。
     * <p>マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュには、
     * 基本的にこのメソッドを使って生成したRefreshingStrategyをセットするようにして下さい。
     * </p>
     *
     * @param cache キャッシュインスタンス。
     * @return RefreshingStrategyインスタンス。
     */
    <K, T> RefreshingStrategy<K, T> newRefreshingStrategyForVolatileProvider(
        Cache<K, T> cache);


    /**
     * 新しいCacheインスタンスを生成して返します。
     * <p>このメソッドが返すCacheインスタンスには、
     * {@link #newRefreshingStrategyForVolatileProvider(Cache)}で生成した
     * RefreshingStrategyインスタンスが自動的にセットされますので、
     * マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュとして利用可能です。
     * </p>
     * <p>このメソッドが返すインスタンスは、{@link #register(String, Cache)}によって自動的に登録されます。
     * </p>
     *
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @return Cacheインスタンス。
     */
    <K, T> Cache<K, T> newCache(Class<K> keyClass, Class<T> valueClass);


    /**
     * 新しいCacheインスタンスを生成して返します。
     * <p>このメソッドが返すCacheインスタンスには、
     * {@link #newRefreshingStrategyForVolatileProvider(Cache)}で生成した
     * RefreshingStrategyインスタンスが自動的にセットされますので、
     * マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュとして利用可能です。
     * </p>
     * <p>このメソッドが返すインスタンスは、{@link #register(String, Cache)}によって自動的に登録されます。
     * </p>
     *
     * @param indexCache キーのインデックスの型。
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @return Cacheインスタンス。
     */
    <I, K extends IndexedCacheKey<I>, T> IndexedCache<I, K, T> newIndexedCache(
        Class<I> indexClass, Class<K> keyClass, Class<T> valueClass);


    /**
     * 指定されたキャッシュを登録します。
     * <p>登録することで、キャッシュの状態を閲覧したり、リフレッシュしたりという操作を外部から行なえるようになります。
     * </p>
     *
     * @param id キャッシュのID。
     * @param cache 登録するキャッシュインスタンス。
     */
    void register(String id, ManagedCache cache);


    /**
     * 登録されているキャッシュインスタンスの配列を返します。
     * <p>キャッシュが1つも登録されていない場合は空の配列を返します。
     * </p>
     *
     * @return 登録されているキャッシュインスタンスの配列。
     */
    ManagedCache[] getCaches();


    /**
     * 登録されている全てのキャッシュをリフレッシュします。
     */
    void refreshCaches();


    /**
     * 登録されている全てのキャッシュをpingします。
     */
    void pingCaches();
}
