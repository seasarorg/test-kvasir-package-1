package org.seasar.kvasir.base.webapp.extension;

/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * RequestProcessor登録のための拡張ポイントのIDです。
     */
    String REQUESTPROCESSORS = "requestProcessors";

    /**
     * RequestFilter登録のための拡張ポイントのIDです。
     */
    String REQUESTFILTERS = "requestFilters";

    /**
     * ExceptionHandler登録のための拡張ポイントのIDです。
     */
    String EXCEPTIONHANDLERS = "exceptionHandlers";

    /**
     * StaticContent登録のための拡張ポイントのIDです。
     */
    String STATICCONTENTS = "staticContents";

    /**
     * サイトで共通に使われるJavaScriptを登録するための拡張ポイントのIDです。
     */
    String JAVASCRIPTS = "javascripts";

    /**
     * サイトで共通に使われるCSSを登録するための拡張ポイントのIDです。
     */
    String CSSS = "csss";

    /**
     * サイトで実際に使うJavaScriptを宣言するための拡張ポイントのIDです。
     */
    String USEJAVASCRIPTS = "useJavascripts";

    /**
     * サイトで実際に使うCSSを宣言するための拡張ポイントのIDです。
     */
    String USECSSS = "useCsss";
}
