package org.seasar.kvasir.base.cache;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ObjectProvider<K, T>
{
    /**
     * 指定されたエントリに対応する最新のオブジェクトを取得し、
     * CachedEntryにラップして返します。
     * <p>指定されたキーに対応するオブジェクトは、ObjectProviderによって生成されます。
     * </p>
     * <p>指定されたキーに対応するオブジェクトが存在しない場合は、
     * nullを値として持つようなCachedEntryが返されます。
     * </p>
     *
     * @param key キー。
     * @return CachedEntry。nullが返されることはありません。
     */
    CachedEntry<K, T> get(K key);


    /**
     * 指定されたエントリを最新の状態に更新します。
     * <p>指定されたエントリに対応するオブジェクトが更新されていない場合、
     * 引数に指定されたエントリをそのまま返します。
     * 対応するオブジェクトが更新されている場合は最新のオブジェクトを取得し、
     * 新たなCachedEntryにラップして返します。
     * </p>
     * <p>指定されたエントリに対応するオブジェクトが既に存在しない場合は、
     * nullを値として持つようなCachedEntryが返されます。
     * </p>
     *
     * @param entry エントリ。
     * @return CachedEntry。nullが返されることはありません。
     */
    CachedEntry<K, T> refresh(CachedEntry<K, T> entry);


    boolean isModified(CachedEntry<K, T> entry);


    /**
     * 指定されたキーとオブジェクトを持つようなCachedEntryを生成して返します。
     * <p>このメソッドは、ObjectProviderの外部で生成されたオブジェクトをキャッシュしたい場合に、
     * CachedEntryオブジェクトを構築するために使用されます。
     * </p>
     *
     * @param key キー。
     * @param object オブジェクト。nullを指定することもできます。
     * @return CachedEntry。nullが返されることはありません。
     */
    CachedEntry<K, T> newEntry(K key, T object);
}
