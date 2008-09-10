package org.seasar.kvasir.base.plugin;

/**
 * プラグインの設定が変更された通知を受け取るためのリスナを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SettingsListener<S>
{
    /**
     * プラグインの設定が変更された場合に呼び出されます。
     * <p>少なくともこのメソッド呼び出し中は設定が再度変更されないことが保証されています。
     * </p>
     * 
     * @param event 設定の変更に関する情報。
     */
    void notifyUpdated(SettingsEvent<S> event);
}
