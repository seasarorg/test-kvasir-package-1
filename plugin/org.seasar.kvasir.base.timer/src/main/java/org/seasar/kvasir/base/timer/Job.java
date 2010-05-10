package org.seasar.kvasir.base.timer;

/**
 * 非同期に実行されるジョブを表わすインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 * 
 * @author skirnir
 */
public interface Job
    extends Runnable
{
    void init();


    void destroy();
}
