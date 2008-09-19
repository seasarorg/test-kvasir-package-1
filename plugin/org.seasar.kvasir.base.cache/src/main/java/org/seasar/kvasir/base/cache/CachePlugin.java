package org.seasar.kvasir.base.cache;

import org.seasar.kvasir.base.cache.setting.CachePluginSettings;
import org.seasar.kvasir.base.plugin.Plugin;


/**
 * <p><b>同期化：</b>
 * TODO このクラス／インタフェースがスレッドセーフかどうかについて記述して下さい。
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * このクラスはスレッドセーフです。
 * このクラスは状態の変更を伴う場合スレッドセーフではありません。
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
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
     * @param id キャッシュのID。nullを指定することもできます。
     * @param cache キャッシュインスタンス。
     * @return RefreshingStrategyインスタンス。
     */
    <K, T> RefreshingStrategy<K, T> newRefreshingStrategyForVolatileProvider(
        String id, Cache<K, T> cache);


    /**
     * 新しいCacheインスタンスを生成して返します。
     * <p>このメソッドが返すCacheインスタンスには{@link CacheStorage}が自動的に設定されます。
     * </p>
     * <p>このメソッドが返すCacheインスタンスには、
     * {@link #newRefreshingStrategyForVolatileProvider(Cache)}で生成した
     * RefreshingStrategyインスタンスが自動的にセットされますので、
     * マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュとして利用可能です。
     * </p>
     * <p>registerがtrueの場合、このメソッドが返すインスタンスは
     * {@link #register(String, Cache)}によって自動的に登録されます。
     * </p>
     *
     * @param id キャッシュのID。nullを指定することもできます。
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @param register 生成したキャッシュを登録するかどうか。通常はtrueを指定するようにして下さい。
     * @return Cacheインスタンス。
     */
    <K, T> Cache<K, T> newCache(String id, Class<K> keyClass,
        Class<T> valueClass, boolean register);


    /**
     * 新しいCacheインスタンスを生成して返します。
     * <p>このメソッドが返すCacheインスタンスには{@link IndexedCacheStorage}が自動的に設定されます。
     * </p>
     * <p>このメソッドが返すCacheインスタンスには、
     * {@link #newRefreshingStrategyForVolatileProvider(Cache)}で生成した
     * RefreshingStrategyインスタンスが自動的にセットされますので、
     * マルチインスタンス構成でKvasir/Soraを稼動させた場合に
     * 他のインスタンスから変更されうるようなオブジェクトを提供するオブジェクトプロバイダを持つキャッシュとして利用可能です。
     * </p>
     * <p>registerがtrueの場合、このメソッドが返すインスタンスは
     * {@link #register(String, Cache)}によって自動的に登録されます。
     * </p>
     *
     * @param id キャッシュのID。nullを指定することもできます。
     * @param indexCache キーのインデックスの型。
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @param register 生成したキャッシュを登録するかどうか。通常はtrueを指定するようにして下さい。
     * @return Cacheインスタンス。
     */
    <I, K extends IndexedCacheKey<I>, T> IndexedCache<I, K, T> newIndexedCache(
        String id, Class<I> indexClass, Class<K> keyClass, Class<T> valueClass,
        boolean register);


    /**
     * 新しい{@linke CacheStorage}インスタンスを生成して返します。
     * 
     * @param id キャッシュのID。nullを指定することもできます。
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @return CacheStorageインスタンス。
     */
    <K, T> CacheStorage<K, T> newCacheStorage(String id, Class<K> keyClass,
        Class<T> valueClass);


    /**
     * 新しい{@linke IndexedCacheStorage}インスタンスを生成して返します。
     * 
     * @param id キャッシュのID。nullを指定することもできます。
     * @param indexCache キーのインデックスの型。
     * @param keyClass キャッシュのキーの型。
     * @param valueClass キャッシュで保持する値の型。
     * @return IndexedCacheStorageインスタンス。
     */
    <I, K extends IndexedCacheKey<I>, T> IndexedCacheStorage<I, K, T> newIndexedCacheStorage(
        String id, Class<I> indexClass, Class<K> keyClass, Class<T> valueClass);


    /**
     * 指定されたキャッシュを登録します。
     * <p>登録することで、キャッシュの状態を閲覧したり、リフレッシュしたりという操作を外部から行なえるようになります。
     * </p>
     *
     * @param id キャッシュのID。nullを指定することもできます。
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
