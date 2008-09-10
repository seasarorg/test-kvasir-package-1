package org.seasar.kvasir.base.extension;

import org.seasar.kvasir.base.Kvasir;


/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * SessionListener登録のための拡張ポイントのIDです。
     */
    String SESSIONLISTENERS = Kvasir.ID + ".sessionListeners";

    /**
     * LifecycleListener登録のための拡張ポイントのIDです。
     */
    String LIFECYCLELISTENERS = Kvasir.ID + ".lifecycleListeners";

    /**
     * CorePlugin登録のための拡張ポイントのIDです。
     */
    String COREPLUGINS = Kvasir.ID + ".corePlugins";
}
