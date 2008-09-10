package org.seasar.kvasir.cms.processor;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスは読み出し操作に関してスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageProcessorLifecycleListener
{
    /**
     * 全てのPageProcessorの初期化が終了した時に呼び出されます。
     */
    void notifyPageProcessorsStarted();
}
