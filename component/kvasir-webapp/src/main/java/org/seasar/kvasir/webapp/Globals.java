package org.seasar.kvasir.webapp;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Globals
{
    String PLUGINID = "org.seasar.kvasir.base.webapp";

    String PLUGINID_PATH = PLUGINID.replace('.', '/');

    String KVASIR_HOME_PATH = "/kvasir";

    /**
     * RequestFilterの呼び出しフェーズを定義するプロパティのキーです。
     */
    String PROP_REQUESTFILTER_PHASES = "requestFilter.phases";

    /**
     * RequestFilterのデフォルトの呼び出しフェーズを指定するプロパティのキーです。
     */
    String PROP_REQUESTFILTER_PHASE_DEFAULT = "requestFilter.phase.default";

    /**
     * RequestProcessorの呼び出しフェーズを定義するプロパティのキーです。
     */
    String PROP_REQUESTPROCESSOR_PHASES = "requestProcessor.phases";

    /**
     * RequestProcessorのデフォルトの呼び出しフェーズを指定するプロパティのキーです。
     */
    String PROP_REQUESTPROCESSOR_PHASE_DEFAULT = "requestProcessor.phase.default";

    String PROP_WEBAPP_BASEURL = "webapp.baseURL";

    String PROP_CONTEXTPATH = "webapp.contextPath";

    /**
     * KvasirにExceptionHandlerの配列を格納するためのキーです。
     */
    String ATTR_EXCEPTIONHANDLERS = PLUGINID + ".exceptionHandlers";

    String ATTR_REQUESTFILTERS = PLUGINID + ".requestFilters";

    /**
     * KvasirにRequestProcessorの配列を格納するためのキーです。
     */
    String ATTR_REQUESTPROCESSORS = PLUGINID + ".requestProcessors";

    /**
     * HttpSessionにLocaleオブジェクトを格納するためのキーです。
     */
    String ATTR_LOCALE = PLUGINID + ".locale";

    /**
     * Exception発生時にExceptionオブジェクトを格納するためのキーです。
     */
    String ATTR_EXCEPTION = PLUGINID + ".exception";
}
