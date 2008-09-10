package org.seasar.kvasir.base;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスは読み出し操作に関してスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface KvasirLifecycleListener
{
    /**
     * Kvasir/Soraが開始され、全ての初期化が終了した時に呼び出されます。
     */
    void notifyKvasirStarted();


    /**
     * Kvasir/Soraが終了処理を開始する直前に呼び出されます。
     */
    void notifyKvasirStopping();
}
