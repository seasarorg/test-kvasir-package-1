package org.seasar.kvasir.cms.util;

import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.html.HTMLUtils;


public class ServletUtils extends org.seasar.kvasir.webapp.util.ServletUtils
{
    private static CmsPlugin cmsPlugin;


    protected ServletUtils()
    {
    }


    protected static CmsPlugin getCmsPlugin()
    {
        if (cmsPlugin == null) {
            cmsPlugin = Asgard.getKvasir().getPluginAlfr().getPlugin(
                CmsPlugin.class);
        }
        return cmsPlugin;
    }


    /**
     * 現在処理中のHTTPリクエストの、オリジナルのコンテキストパスを返します。
     * <p>VirtualHttpServletRequestを利用しているかによらず、
     * オリジナルのコンテキストパスを返します。
     * </p>
     * <p>現在システムがHTTPリクエストの処理中でない場合はnullを返します。
     * </p>
     *
     * @return オリジナルのコンテキストパス。
     */
    public static String getOriginalContextPath()
    {
        return getOriginalContextPath(getHttpServletRequest());
    }


    /**
     * オリジナルのコンテキストパスを返します。
     * <p>VirtualHttpServletRequestを利用しているかによらず、
     * オリジナルのコンテキストパスを返します。
     * </p>
     *
     * @param request リクエスト。
     * @return オリジナルのコンテキストパス。
     */
    public static String getOriginalContextPath(HttpServletRequest request)
    {
        if (request == null) {
            return null;
        }
        Object contextPath = request.getAttribute(CmsPlugin.ATTR_CONTEXT_PATH);
        if (contextPath instanceof String) {
            return (String)contextPath;
        } else {
            return request.getContextPath();
        }
    }


    /**
     * Kvasir WebアプリケーションのルートURLを返します。
     * <p><code>request</code>がVirtualHttpServletRequestの場合でも、
     * Gard Rootではなく、実際のKvasirのルートを表すURLを返します。
     * </p>
     * <p>返されるURLは、プロトコルやドメイン名を含むURLです。
     * また末尾に「/」はつきません。
     * </p>
     *
     * @param request リクエストオブジェクト。
     * @return ルートURL。
     */
    public static String getWebappURL(HttpServletRequest request)
    {
        if (request == null) {
            return null;
        }

        return getDomainURL(request) + getOriginalContextPath(request);
    }


    public static String getWebappURL()
    {
        return getWebappURL(getHttpServletRequest());
    }


    public static String getWebappURL(int heimId)
    {
        return getWebappURL(heimId, getHttpServletRequest());
    }


    public static String getWebappURL(int heimId, HttpServletRequest request)
    {
        return getDomainURL(heimId) + getOriginalContextPath(request);
    }


    public static String getDomainURL(int heimId)
    {
        String site = getCmsPlugin().getSite(heimId);
        if (site != null) {
            return site;
        } else {
            return getDomainURL();
        }
    }


    /**
     * 指定されたパス名に対応するWebページを表すURLを返します。
     *
     * @param pathname パス名。
     * @param request リクエストオブジェクト。VirtualHttpServletRequestを渡すこともできますが、
     * その場合でもGard Rootを仮想的なルートとはみなしません。
     *
     * @return 指定されたパス名に対応するWebページを表すURL。
     */
    public static String getURL(String pathname, HttpServletRequest request)
    {
        return getWebappURL(request) + pathname;
    }


    public static String getURL(String pathname)
    {
        return getURL(pathname, getHttpServletRequest());
    }


    public static String getURL(int heimId, String pathname,
        HttpServletRequest request)
    {
        return getWebappURL(heimId, request) + pathname;
    }


    public static String getURL(int heimId, String pathname)
    {
        return getWebappURL(heimId) + pathname;
    }


    /**
     * 指定されたPageオブジェクトに対応するWebページを表すURLを返します。
     * <p>Pageオブジェクトがnodeである場合はURLの末尾に「/」がつきます。
     * </p>
     * <p>Pageオブジェクトまたはリクエストオブジェクトとしてnullが指定されるとnullを返します。
     * </p>
     *
     * @param page Pageオブジェクト。
     * @param request リクエストオブジェクト。VirtualHttpServletRequestを渡すこともできますが、
     * その場合でもGard Rootを仮想的なルートとはみなしません。
     *
     * @return 指定されたパス名に対応するWebページを表すURL。
     */
    public static String getURL(Page page, HttpServletRequest request)
    {
        if (page == null || request == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getWebappURL(page.getHeimId(), request)).append(
            page.getPathname());
        if (page.isNode()) {
            sb.append('/');
        }
        return sb.toString();
    }


    /**
     * 指定されたPageオブジェクトに対応するWebページを表すURLを返します。
     * <p>Pageオブジェクトがnodeである場合はURLの末尾に「/」がつきます。
     * </p>
     * <p>Pageオブジェクトとしてnullが指定されるとnullを返します。
     * 現在システムがHTTPリクエストの処理中でない場合もnullを返します。
     * </p>
     *
     * @param page Pageオブジェクト。
     *
     * @return 指定されたパス名に対応するWebページを表すURL。
     */
    public static String getURL(Page page)
    {
        return getURL(page, getHttpServletRequest());
    }


    /**
     * 指定されたパスに対応するURIを返します。
     * <p>URIはコンテキストパスとパスを連結したものになります。
     * </p>
     * <p>パスまたはリクエストオブジェクトとしてnullが指定されるとnullを返します。
     * </p>
     *
     * @param pathname パス。nullを指定することもできます。リクエストパラメータつきのパスを指定することもできます。
     * @param request リクエストオブジェクト。
     *
     * @return 指定されたパスに対応するURI。
     */
    public static String getURI(String pathname, HttpServletRequest request)
    {
        if (pathname == null || request == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String trimmed = HTMLUtils.trimQueryAndParameter(pathname);
        sb.append(getOriginalContextPath(request)).append(trimmed);
        if (sb.length() == 0) {
            sb.append('/');
        }
        sb.append(pathname.substring(trimmed.length()));
        return sb.toString();
    }


    /**
     * 指定されたパスに対応するURIを返します。
     * <p>URIはコンテキストパスとパスを連結したものになります。
     * </p>
     * <p>パスとしてnullが指定されるとnullを返します。
     * また、現在システムがHTTPリクエストの処理中でない場合もnullを返します。
     * </p>
     *
     * @param pathname パス。nullを指定することもできます。リクエストパラメータつきのパスを指定することもできます。
     *
     * @return 指定されたパスに対応するURI。
     */
    public static String getURI(String pathname)
    {
        return getURI(pathname, getHttpServletRequest());
    }


    /**
     * 指定されたPageオブジェクトに対応するURIを返します。
     * <p>URIはコンテキストパスとPageオブジェクトのパス名を連結したものになります。
     * </p>
     * <p>Pageオブジェクトがnodeである場合はURIの末尾に「/」がつきます。
     * </p>
     * <p>Pageオブジェクトまたはリクエストオブジェクトとしてnullが指定されるとnullを返します。
     * </p>
     *
     * @param page Pageオブジェクト。nullを指定することもできます。
     * @param request リクエストオブジェクト。
     *
     * @return 指定されたパスに対応するURI。
     */
    public static String getURI(Page page, HttpServletRequest request)
    {
        if (page == null || request == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(page.getPathname());
        if (page.isNode()) {
            sb.append('/');
        }
        return getURI(sb.toString(), request);
    }


    /**
     * 指定されたPageオブジェクトに対応するURIを返します。
     * <p>URIはコンテキストパスとPageオブジェクトのパス名を連結したものになります。
     * </p>
     * <p>Pageオブジェクトがnodeである場合はURIの末尾に「/」がつきます。
     * </p>
     * <p>Pageオブジェクトとしてnullが指定されるとnullを返します。
     * また、現在システムがHTTPリクエストの処理中でない場合もnullを返します。
     * </p>
     *
     * @param page Pageオブジェクト。nullを指定することもできます。
     *
     * @return 指定されたパスに対応するURI。
     */
    public static String getURI(Page page)
    {
        return getURI(page, getHttpServletRequest());
    }
}
