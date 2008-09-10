package org.seasar.kvasir.base.webapp.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.base.webapp.WebappPlugin;
import org.seasar.kvasir.webapp.util.ServletUtils;


public class PresentationUtils
{
    /**
     * セッションの中で一時的にHTMLに追加したいJavaScriptのパス名の配列を保持するための
     * HttpSessionの属性名です。
     */
    public static final String ATTR_TEMPORAL_SCRIPTS = WebappPlugin.ID
        + ".temporalScripts";

    /**
     * セッションの中で一時的にHTMLに追加したいCSSのパス名の配列を保持するための
     * HttpSessionの属性名です。
     */
    public static final String ATTR_TEMPORAL_STYLES = WebappPlugin.ID
        + ".temporalStyles";

    private static WebappPlugin webappPlugin_;


    protected PresentationUtils()
    {
    }


    /**
     * サイト共通で使用されるJavaScriptのパス名を返します。
     *
     * @return JavaScriptのパス名の配列。nullを返すことはありません。
     */
    public static String[] getJavascriptPathnames()
    {
        String[] pathnames = getPlugin().getJavascriptPathnames();
        String[] temporalPathnames = getSessionScopeArray(ATTR_TEMPORAL_SCRIPTS);
        if (temporalPathnames.length == 0) {
            return pathnames;
        } else {
            Set<String> set = new LinkedHashSet<String>();
            set.addAll(Arrays.asList(pathnames));
            set.addAll(Arrays.asList(temporalPathnames));
            return set.toArray(new String[0]);
        }
    }


    static String[] getSessionScopeArray(String name)
    {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request == null) {
            return new String[0];
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return new String[0];
        }
        String[] array = (String[])session.getAttribute(name);
        if (array == null) {
            return new String[0];
        } else {
            return array;
        }
    }


    /**
     * サイト共通で使用されるCSSのパス名を返します。
     *
     * @return CSSのパス名の配列。nullを返すことはありません。
     */
    public static String[] getCssPathnames()
    {
        String[] pathnames = getPlugin().getCssPathnames();
        String[] temporalPathnames = getSessionScopeArray(ATTR_TEMPORAL_STYLES);
        if (temporalPathnames.length == 0) {
            return pathnames;
        } else {
            Set<String> set = new LinkedHashSet<String>();
            set.addAll(Arrays.asList(pathnames));
            set.addAll(Arrays.asList(temporalPathnames));
            return set.toArray(new String[0]);
        }
    }


    static WebappPlugin getPlugin()
    {
        if (webappPlugin_ != null) {
            return webappPlugin_;
        } else {
            return Asgard.getKvasir().getPluginAlfr().getPlugin(
                WebappPlugin.class);
        }
    }


    @ForTest
    static void setPlugin(WebappPlugin webappPlugin)
    {
        webappPlugin_ = webappPlugin;
    }
}
