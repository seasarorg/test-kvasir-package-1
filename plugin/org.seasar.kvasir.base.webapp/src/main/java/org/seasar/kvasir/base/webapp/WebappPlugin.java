package org.seasar.kvasir.base.webapp;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface WebappPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.base.webapp";

    String ID_PATH = ID.replace('.', '/');

    String PROP_REQUESTFILTER_PHASES = "requestFilter.phases";

    String PROP_REQUESTFILTER_PHASE_DEFAULT = "requestFilter.phase.default";

    String PROP_REQUESTPROCESSOR_PHASES = "requestProcessor.phases";

    String PROP_REQUESTPROCESSOR_PHASE_DEFAULT = "requestProcessor.phase.default";

    String PATH_PLUGINS = "/plugins";

    String PATH_JS = PATH_PLUGINS + "/js";

    String PATH_CSS = PATH_PLUGINS + "/css";


    /**
     * 静的コンテントを追加します。
     * <p>指定されたパスで静的コンテントをサイトに追加します。
     * 例えば<code>path</code>として「<code>/path/to/image.jpg</code>」を指定した場合、
     * サイトトップのURLを<code>http://localhost:9846/</code>として
     * <code>http://localhost:9846/path/to/image.jpg</code>にアクセスすると、
     * <code>content</code>で指定したコンテントがHTTPレスポンスとして返されるようになります。
     * </p>
     *
     * @param path 追加先のパス。
     * @param content 追加するコンテント。
     */
    void registerStaticContent(String path, Content content);


    String[] getJavascriptPathnames();


    boolean containsJavascript(String basePath, String id, String moduleName);


    boolean containsJavascript(String id, String moduleName);


    boolean containsJavascript(String path);


    String[] getCssPathnames();


    boolean containsCss(String basePath, String id, String moduleName);


    boolean containsCss(String id, String moduleName);


    boolean containsCss(String path);
}
