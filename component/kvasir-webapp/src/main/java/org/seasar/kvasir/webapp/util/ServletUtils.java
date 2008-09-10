package org.seasar.kvasir.webapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.util.html.HTMLUtils;
import org.seasar.kvasir.webapp.servlet.HttpServletResponseGrabber;
import org.seasar.kvasir.webapp.servlet.ThreadLocalServletContext;


/**
 * @author YOKOTA Takehiko
 */
public class ServletUtils
{
    public static final String ATTR_INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";

    public static final String ATTR_INCLUDE_PATH_INFO = "javax.servlet.include.path_info";

    public static final String ATTR_INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";

    public static final String ATTR_INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

    public static final String ATTR_FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";

    public static final String ATTR_FORWARD_PATH_INFO = "javax.servlet.forward.path_info";

    public static final String ATTR_FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";

    public static final String ATTR_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";

    private static final Set<String> PASSTHROUGH_HEADERNAME_SET;

    static {
        Set<String> passthroughHeaderNameSet = new HashSet<String>();
        passthroughHeaderNameSet.add("host");
        passthroughHeaderNameSet.add("user-agent");
        passthroughHeaderNameSet.add("accept");
        passthroughHeaderNameSet.add("accept-language");
        passthroughHeaderNameSet.add("accept-charset");
        PASSTHROUGH_HEADERNAME_SET = Collections
            .unmodifiableSet(passthroughHeaderNameSet);
    }


    protected ServletUtils()
    {
    }


    public static String getRequestContextPath(HttpServletRequest request)
    {
        return request.getContextPath();
    }


    public static String getRequestPath(HttpServletRequest request)
    {
        String path = getNativeRequestPath(request);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }


    public static String getNativeRequestPath(HttpServletRequest request)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getServletPath());
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            sb.append(pathInfo);
        }
        adjustNativePath(sb, request.getRequestURI());
        return sb.toString();
    }


    // servlet-mappingが'/*'の時は、コンテキストルート（'/'なし）への
    // リクエストを受けてもpathInfoは'/'になってしまう。この場合でも正しく
    // 元々のパスを取得できるように補正をする。
    static void adjustNativePath(StringBuilder sb, String requestURI)
    {
        if (HTMLUtils.trimQueryAndParameter(requestURI).endsWith("/")) {
            if (sb.length() == 0 || sb.charAt(sb.length() - 1) != '/') {
                sb.append('/');
            }
        } else {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
                sb.delete(sb.length() - 1/*= "/".length() */, sb.length());
            }
        }
    }


    public static String getContextPath(HttpServletRequest request)
    {
        String contextPath = getIncludeContextPath(request);
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        return contextPath;
    }


    public static String getPath(HttpServletRequest request)
    {
        String path = getNativePath(request);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }


    public static String getNativePath(HttpServletRequest request)
    {
        StringBuilder sb = new StringBuilder();
        String servletPath = getIncludeServletPath(request);
        String pathInfo;
        String requestURI;
        if (servletPath != null) {
            pathInfo = getIncludePathInfo(request);
            requestURI = getIncludeRequestURI(request);
        } else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
            requestURI = request.getRequestURI();
        }
        sb.append(servletPath);
        if (pathInfo != null) {
            sb.append(pathInfo);
        }

        adjustNativePath(sb, requestURI);
        return sb.toString();
    }


    /**
     * インクルード先のパスに関するコンテキストパスを返します。
     * <p>
     * 現在のディスパッチ状態がインクルードでない場合はnullを返します。
     * </p>
     *
     * @param request リクエスト。
     * @return コンテキストパス。
     */
    public static String getIncludeContextPath(HttpServletRequest request)
    {
        return (String)request.getAttribute(ATTR_INCLUDE_CONTEXT_PATH);
    }


    public static String getIncludePathInfo(HttpServletRequest request)
    {
        return (String)request.getAttribute(ATTR_INCLUDE_PATH_INFO);
    }


    public static String getIncludeServletPath(HttpServletRequest request)
    {
        return (String)request.getAttribute(ATTR_INCLUDE_SERVLET_PATH);
    }


    public static String getIncludeRequestURI(HttpServletRequest request)
    {
        return (String)request.getAttribute(ATTR_INCLUDE_REQUEST_URI);
    }


    public static String constructURI(String path,
        Map<String, Object> paramMap, String encoding)
    {
        if (paramMap == null) {
            return path;
        }

        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        StringBuilder sb = new StringBuilder(path);
        String delim = "?";
        Iterator<Map.Entry<String, Object>> itr = paramMap.entrySet()
            .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Object> entry = itr.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            String encodedKey = HTMLUtils.encodeURL(key, encoding);
            String[] values;
            if (value instanceof String[]) {
                values = (String[])value;
            } else {
                values = new String[] { value.toString() };
            }
            for (int i = 0; i < values.length; i++) {
                sb.append(delim);
                delim = "&";
                sb.append(encodedKey);
                sb.append("=");
                sb.append(HTMLUtils.encodeURL(values[i], encoding));
            }
        }

        return sb.toString();
    }


    public static boolean isIncluded(HttpServletRequest request)
    {
        return (request.getAttribute(ATTR_INCLUDE_REQUEST_URI) != null);
    }


    public static boolean isForwarded(HttpServletRequest request)
    {
        return (request.getAttribute(ATTR_FORWARD_REQUEST_URI) != null);
    }


    public static void setNoCache(HttpServletResponse response)
    {
        if (!response.containsHeader("Cache-Control")) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control",
                "no-cache,no-store,must-revalidate");
            response.setDateHeader("Expires", 1);
        }
    }


    /**
     * 指定されたパスに対応するコンテンツを文字列として返します。
     * <p>対応するディスパッチャが存在しないパスを指定した場合はnullを返します。
     * </p>
     * <p>コンテンツの取得に失敗した場合はRuntimeExceptionがスローされます。</p>
     * <p>このメソッドの返り値は必ずしもHTTPクライアントからリクエストされた場合の結果と一致しません。
     * HTTPクライアントからリクエストされた場合の結果と同じものを確実に取得するには、
     * {@link #getResponseText(String, HttpServletRequest, HttpServletResponse)}
     * を使って下さい。
     * </p>
     *
     * @param request HttpServletRequest。
     * @param response HttpServletResponse。
     * @param path コンテキスト相対のパス。
     * @return パスに対応するコンテンツ。
     * @see #getResponseText(String, HttpServletRequest, HttpServletResponse)
     */
    public static String grabAsString(HttpServletRequest request,
        HttpServletResponse response, String path)
    {
        if (path.length() == 0) {
            path = "/";
        }
        RequestDispatcher rd = request.getRequestDispatcher(path);
        if (rd == null) {
            return null;
        }
        HttpServletResponseGrabber grabber = new HttpServletResponseGrabber(
            response);
        try {
            rd.include(request, grabber);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ServletException ex) {
            throw new RuntimeException(ex);
        }
        return grabber.getContentString();
    }


    public static ServletContext getOriginalServletContext()
    {
        Object application = ComponentContainerFactory.getApplication();
        if (application instanceof ThreadLocalServletContext) {
            return ((ThreadLocalServletContext)application)
                .getOriginalServletContext();
        } else if (application instanceof ServletContext) {
            return (ServletContext)application;
        } else {
            return null;
        }
    }


    public static ServletContext getContextServletContext()
    {
        Object application = ComponentContainerFactory.getApplication();
        if (application instanceof ThreadLocalServletContext) {
            return ((ThreadLocalServletContext)application)
                .getContextServletContext();
        } else {
            throw new RuntimeException("May logic error");
        }
    }


    public static void setContextServletContext(ServletContext servletContext)
    {
        Object application = ComponentContainerFactory.getApplication();
        if (application instanceof ThreadLocalServletContext) {
            ((ThreadLocalServletContext)application)
                .setContextServletContext(servletContext);
        } else {
            throw new RuntimeException("May logic error");
        }
    }


    /**
     * 指定されたURLに対応するレスポンスボディを返します。
     * <p>現在のセッションやリクエストヘッダの値を使わずにレスポンスを取得します。
     * </p>
     *
     * @param urlString URL。
     * @return レスポンスボディ。
     */
    public static String getResponseText(String urlString)
    {
        return getResponseText(urlString, null, null);
    }


    /**
     * 指定されたURLに対応するレスポンスボディを返します。
     * <p><code>request</code>が持つセッションIDやリクエストヘッダの値を使って
     * レスポンスを取得します。
     * </p>
     *
     * @param urlString URL。
     * @param request リクエストオブジェクト。
     * @param response レスポンスオブジェクト。
     * @return レスポンスボディ。
     * @see #grabAsString(HttpServletRequest, HttpServletResponse, String)
     */
    @SuppressWarnings("unchecked")
    public static String getResponseText(String urlString,
        HttpServletRequest request, HttpServletResponse response)
    {
        Response resp = getResponse(urlString, request, response);
        InputStream in = resp.getInputStream();
        String charset = resp.getCharset();
        return HTMLUtils.readHTML(in, charset, false);
    }


    /**
     * 指定されたURLに対応するレスポンスを返します。
     * <p>現在のセッションやリクエストヘッダの値を使わずにレスポンスを取得します。
     * </p>
     *
     * @param urlString URL。
     * @return レスポンス。
     */
    public static Response getResponse(String urlString)
    {
        return getResponse(urlString, null, null);
    }


    /**
     * 指定されたURLに対応するレスポンスを返します。
     * <p><code>request</code>が持つセッションIDやリクエストヘッダの値を使って
     * レスポンスを取得します。
     * </p>
     *
     * @param urlString URL。
     * @param request リクエストオブジェクト。
     * @param response レスポンスオブジェクト。
     * @return レスポンス。
     * @see #getResponseText(String, HttpServletRequest, HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    public static Response getResponse(String urlString,
        HttpServletRequest request, HttpServletResponse response)
    {
        boolean withDetail = (request != null && response != null);
        // セッションIDを付与する。
        String jsessionid = null;
        if (withDetail) {
            String encoded = response.encodeURL(urlString);
            if (encoded.equals(urlString)) {
                // URLにセッションIDが付与されていないので、Cookieに付与する。
                HttpSession session = request.getSession(false);
                if (session != null) {
                    jsessionid = session.getId();
                }
            }
        }
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
        Response resp = new Response();
        URLConnection connection;
        try {
            connection = url.openConnection();
            if (jsessionid != null) {
                connection.addRequestProperty("Cookie", "JSESSIONID="
                    + jsessionid);
            }
            if (withDetail) {
                for (Enumeration<String> enm = request.getHeaderNames(); enm
                    .hasMoreElements();) {
                    String name = enm.nextElement();
                    // FIXME 元々のJSESSIONIDエントリを除去し、現在のJSESSIONIDを追加
                    // するようにしてCookieもリクエストに含めるようにしよう。
                    if (shouldPassthrough(name)) {
                        connection.addRequestProperty(name, request
                            .getHeader(name));
                    }
                }
            }

            if (connection instanceof HttpURLConnection) {
                resp.setStatus(((HttpURLConnection)connection)
                    .getResponseCode());
            }
            resp.setContentType(connection.getContentType());
            //            Object rawContent = connection.getContent();
            //            if (rawContent instanceof InputStream) {
            //                resp.setInputStream((InputStream)rawContent);
            //            }
            resp.setInputStream(connection.getInputStream());
        } catch (IOException ignore) {
        }
        return resp;
    }


    static boolean shouldPassthrough(String headerName)
    {
        return PASSTHROUGH_HEADERNAME_SET.contains(headerName.toLowerCase());
    }


    public static HttpServletRequest getHttpServletRequest()
    {
        Object request = Asgard.getKvasir().getRootComponentContainer()
            .getRequest();
        if (request instanceof HttpServletRequest) {
            return (HttpServletRequest)request;
        } else {
            return null;
        }
    }


    public static HttpServletResponse getHttpServletResponse()
    {
        Object request = Asgard.getKvasir().getRootComponentContainer()
            .getResponse();
        if (request instanceof HttpServletResponse) {
            return (HttpServletResponse)request;
        } else {
            return null;
        }
    }


    /**
     * リクエストされたURLのうちプロトコルとドメイン名までの部分文字列を返します。
     * <p>返されるURLの末尾に「/」はつきません。
     * </p>
     *
     * @param request リクエストオブジェクト。
     * @return URLのドメイン名までの部分。
     */
    public static String getDomainURL(HttpServletRequest request)
    {
        if (request == null) {
            return null;
        }
        // Tomcat5.0系ではrequest.getRequestURI()とrequest.getRequestURL()
        // の組み合わせをそのまま使うとまずいことがあるためにこうしている。
        // 参考：http://d.hatena.ne.jp/tanigon/20060824
        String uri = getRequestRequestURI(request);
        String url = getRequestRequestURL(request).toString();
        if (!url.endsWith(uri)) {
            throw new RuntimeException("May logic error.");
        }
        return url.substring(0, url.length() - uri.length());
    }


    public static String getDomainURL()
    {
        return getDomainURL(getHttpServletRequest());
    }


    /**
     * リクエスト時のrequestURIを返します。
     * <p>{@link HttpServletRequest#getRequetURI()}メソッドの返すURIは、
     * フォワード後にはフォワード先のURIを返すようになるため、
     * リクエスト時のrequestURIを取得することができません。
     * このメソッドを使うことによって、常にリクエスト時のrequestURIを取得することができます。
     * </p>
     *
     * @param request リクエスト。
     * @return リクエスト時のrequestURI。
     */
    public static String getRequestRequestURI(HttpServletRequest request)
    {
        if (request == null) {
            return null;
        }
        if (isForwarded(request)) {
            return (String)request.getAttribute(ATTR_FORWARD_REQUEST_URI);
        } else {
            return request.getRequestURI();
        }
    }


    /**
     * リクエスト時のrequestURLを返します。
     * <p>{@link HttpServletRequest#getRequetURL()}メソッドの返すURLは、
     * フォワード後にはフォワード先のURLを返すようになるため、
     * リクエスト時のrequestURLを取得することができません。
     * このメソッドを使うことによって、常にリクエスト時のrequestURLを取得することができます。
     * </p>
     *
     * @param request リクエスト。
     * @return リクエスト時のrequestURL。
     */
    public static StringBuffer getRequestRequestURL(HttpServletRequest request)
    {
        if (request == null) {
            return null;
        }
        if (isForwarded(request)) {
            StringBuffer requestURL = request.getRequestURL();
            String requestURI = request.getRequestURI();
            if (requestURL.toString().endsWith(requestURI)) {
                requestURL.delete(requestURL.length() - requestURI.length(),
                    requestURL.length());
                requestURL.append((String)request
                    .getAttribute(ATTR_FORWARD_REQUEST_URI));
            } else {
                // TODO Tomcat5.0のバグを回避するためのworkaround。完璧ではないが
                // 大体の場合において大丈夫。Tomcat5.0を正式に非対応とした際には上記
                // if文の分岐は不要。
                // （説明：Tomcat5.0ではforward後でもrequest.getRequestURL()がforward
                // 前のURLを返すため、何もしないようにしている。）
            }
            return requestURL;
        } else {
            return request.getRequestURL();
        }
    }


    /**
     * Kvasir WebアプリケーションのルートURLを返します。
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
        return getDomainURL(request) + request.getContextPath();
    }


    public static String getWebappURL()
    {
        return getWebappURL(getHttpServletRequest());
    }


    /**
     * 指定されたパスに対応するURIを返します。
     * <p>URIはコンテキストパスとパスを連結したものになります。
     * </p>
     * <p>パスとしてnullが指定されるとnullを返します。
     * </p>
     *
     * @param pathname パス。nullを指定することもできます。リクエストパラメータつきのパスを指定することもできます。
     * @param request リクエストオブジェクト。
     *
     * @return 指定されたパスに対応するURI。
     */
    public static String getURI(String pathname, HttpServletRequest request)
    {
        if (pathname == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String trimmed = HTMLUtils.trimQueryAndParameter(pathname);
        sb.append(getRequestContextPath(request)).append(trimmed);
        if (sb.length() == 0) {
            sb.append('/');
        }
        sb.append(pathname.substring(trimmed.length()));
        return sb.toString();
    }


    public static String getURI(String pathname)
    {
        return getURI(pathname, getHttpServletRequest());
    }
}
