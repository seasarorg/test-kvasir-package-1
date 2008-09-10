package org.seasar.kvasir.base.cache.impl;

import java.io.Serializable;

import org.seasar.kvasir.base.cache.CachedEntry;


/**
 * キャッシュに保存されるエントリを表すインタフェースの実装です。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 *
 * @author YOKOTA Takehiko
 */
public class CachedEntryImpl<K, T>
    implements CachedEntry<K, T>, Serializable
{
    private static final long serialVersionUID = -820288344631656548L;

    private T cached_;

    private long createdTime_;

    private K key_;

    private long sequenceNumber_;

    private volatile long age_;


    public CachedEntryImpl(K key, long sequenceNumber, T cached)
    {
        key_ = key;
        sequenceNumber_ = sequenceNumber;
        cached_ = cached;

        createdTime_ = System.currentTimeMillis();
        age_ = 1;
    }


    public T getCached()
    {
        return cached_;
    }


    public long getCreatedTime()
    {
        return createdTime_;
    }


    public K getKey()
    {
        return key_;
    }


    public long getSequenceNumber()
    {
        return sequenceNumber_;
    }


    public long getAge()
    {
        return age_;
    }


    public void age()
    {
        age_++;
    }
}
