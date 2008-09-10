package org.seasar.kvasir.base;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスは読み出し操作に関してスレッドセーフである必要があります。</p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SessionListener
{
    /**
     * 現在のスレッドでKvasir/Soraのセッションが開始された時に呼び出されます。
     */
    void notifyBeginSession();


    /**
     * 現在のスレッドでKvasir/Soraのセッションが終了した時に呼び出されます。
     */
    void notifyEndSession();
}
