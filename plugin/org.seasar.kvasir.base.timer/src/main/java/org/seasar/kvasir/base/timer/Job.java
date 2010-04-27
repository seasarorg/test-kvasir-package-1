package org.seasar.kvasir.base.timer;

/**
 * 定期実行されるジョブを表わすインタフェースです。
 * <p>{@link #run()}メソッドはフレームワークによって1分毎に呼び出されます。
 * </p>
 * 
 * @author skirnir
 */
public interface Job
    extends Runnable
{
}
