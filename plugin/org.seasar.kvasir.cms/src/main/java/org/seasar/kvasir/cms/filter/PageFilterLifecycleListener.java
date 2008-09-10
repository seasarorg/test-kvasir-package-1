package org.seasar.kvasir.cms.filter;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスは読み出し操作に関してスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageFilterLifecycleListener
{
    /**
     * 全てのPageFilterの初期化が終了した時に呼び出されます。
     */
    void notifyPageFiltersStarted();
}
