package org.seasar.kvasir.webapp.extension;

import org.seasar.kvasir.webapp.Globals;


/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * RequestFilter登録のための拡張ポイントのIDです。
     */
    String REQUESTFILTERS = Globals.PLUGINID + ".requestFilters";

    /**
     * RequestProcessor登録のための拡張ポイントのIDです。
     */
    String REQUESTPROCESSORS = Globals.PLUGINID + ".requestProcessors";

    /**
     * ExceptionHandler登録のための拡張ポイントのIDです。
     */
    String EXCEPTIONHANDLERS = Globals.PLUGINID + ".exceptionHandlers";
}
