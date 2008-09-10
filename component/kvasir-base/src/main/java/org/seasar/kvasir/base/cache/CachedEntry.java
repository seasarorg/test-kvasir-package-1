package org.seasar.kvasir.base.cache;

/**
 * キャッシュに保存されるエントリを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 *
 * @author YOKOTA Takehiko
 */
public interface CachedEntry<K, T>
{
    K getKey();


    long getSequenceNumber();


    long getCreatedTime();


    long getAge();


    T getCached();


    void age();
}
