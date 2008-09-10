package org.seasar.kvasir.cms.extension;

/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * PageFilter登録のための拡張ポイントのIDです。
     */
    String PAGEFILTERS = "pageFilters";

    /**
     * PageProcessor登録のための拡張ポイントのIDです。
     */
    String PAGEPROCESSORS = "pageProcessors";

    /**
     * PageExceptionHandler登録のための拡張ポイントのIDです。
     */
    String PAGEEXCEPTIONHANDLERS = "pageExceptionHandlers";

    /**
     *  PageProcessorLifecycleListener登録のための拡張ポイントのIDです。
     */
    String PAGEPROCESSORLIFECYCLELISTENERS = "pageProcessorLifecycleListeners";

    /**
     *  PageFilterLifecycleListener登録のための拡張ポイントのIDです。
     */
    String PAGEFILTERLIFECYCLELISTENERS = "pageFilterLifecycleListeners";

    /**
     * PageFilterPhase登録のための拡張ポイントのIDです。
     */
    String PAGEFILTERPHASES = "pageFilterPhases";

    /**
     * PageProcessorPhase登録のための拡張ポイントのIDです。
     */
    String PAGEPROCESSORPHASES = "pageProcessorPhases";
}
