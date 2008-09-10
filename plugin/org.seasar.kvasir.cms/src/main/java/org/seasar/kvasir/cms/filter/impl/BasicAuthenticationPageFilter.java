package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.Base64;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * Basic認証を行なわせるためのPageFilterです。
 * このPageFilterはActorBindingPageFilterよりも前に実行する必要があります。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class BasicAuthenticationPageFilter
    implements PageFilter
{
    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";

    public static final String ATTR_AUTH_TRYING = ".auth.trying";

    private AuthPlugin authPlugin_;

    private CmsPlugin webappPlugin_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
        authPlugin_ = null;
        webappPlugin_ = null;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        if (needAuthentication(request)) {
            // 認証中であるので認証を行なう。
            if (log_.isDebugEnabled()) {
                log_.debug("LOGIN PROCESS: (2) Do authentication");
            }
            User user = authenticate(request);
            if (user == null) {
                // 認証に失敗したかクライアントから認証情報が送られて
                // きていない。
                if (log_.isDebugEnabled()) {
                    log_.debug("LOGIN PROCESS: (2a) Authentication failed"
                        + " or no authentication information exists. Retry.");
                }
            } else {
                // 認証に成功した。
                if (log_.isDebugEnabled()) {
                    log_.debug("LOGIN PROCESS: (2b) Succeed. Finish trying.");
                }
                updateSession(request, user);
            }
        }

        chain.doFilter(request, response, dispatcher, pageRequest);
    }


    /*
     * private scope methods
     */

    private boolean needAuthentication(HttpServletRequest request)
    {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (session.getAttribute(ATTR_AUTH_TRYING) != null);
        } else {
            String authHeader = request.getHeader(HTTP_HEADER_AUTHORIZATION);
            // Cookies非対応のクライアントが
            // sessionなしで毎回Authorization:を送ってくるのをサポートする
            return (authHeader != null);
        }
    }


    private User authenticate(HttpServletRequest request)
    {
        User user = null;

        String authHeader = request.getHeader(HTTP_HEADER_AUTHORIZATION);
        if (log_.isDebugEnabled()) {
            if (authHeader == null) {
                log_.debug("Authentication header from client does not exist");
            } else {
                log_.debug("Authentication header from client exists");
            }
        }
        if (authHeader != null) {
            authHeader = authHeader.trim();
            int space = authHeader.indexOf(" ");
            if (space >= 0
                && authHeader.substring(0, space).equalsIgnoreCase("Basic")) {
                String cred = authHeader.substring(space + 1);
                String decoded = new String(Base64.decode(cred.getBytes()));
                int colon = decoded.indexOf(":");
                if (colon >= 0) {
                    int heimId = CmsUtils.getPageRequest(request).getRootPage()
                        .getHeimId();
                    String name = decoded.substring(0, colon);
                    String password = decoded.substring(colon + 1);
                    user = authPlugin_.authenticate(heimId, name, password);
                }
            }
        }

        return user;
    }


    private void updateSession(HttpServletRequest request, User user)
    {
        if (log_.isDebugEnabled()) {
            log_.debug("UPDATE SESSION: " + user);
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            if (log_.isDebugEnabled()) {
                log_.debug("SESSION INVALIDATED!!!!!!!!!!!");
            }
            session = null;
        }
        if (!user.isAnonymous()) {
            if (log_.isDebugEnabled()) {
                log_.debug("GET SESSION(TRUE)!!!!!!!!!!!");
            }
            webappPlugin_.login(request, user);
        }
    }


    /*
     * for framework
     */

    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void setWebappPlugin(CmsPlugin webappPlugin)
    {
        webappPlugin_ = webappPlugin;
    }
}
