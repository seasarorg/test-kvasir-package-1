package org.seasar.kvasir.cms.handler.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.impl.BasicAuthenticationPageFilter;
import org.seasar.kvasir.cms.handler.PageExceptionHandler;
import org.seasar.kvasir.cms.handler.PageExceptionHandlerChain;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class BasicAuthenticationPageExceptionHandler
    implements PageExceptionHandler
{
    public static final String PROP_ERRORPAGE = "errorPage";

    private String errorPage_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public void init(ExceptionHandlerConfig config)
    {
        errorPage_ = config.getInitParameter(PROP_ERRORPAGE);
    }


    public void destroy()
    {
        errorPage_ = null;
    }


    public void doHandle(HttpServletRequest request,
        HttpServletResponse response, Exception ex, PageRequest pageRequest,
        PageExceptionHandlerChain chain)
    {
        // ログイン処理を開始する。
        log_.debug("LOGIN PROCESS:"
            + " (1) Request authentication information to client");
        setAuthenticationHeader(response, "Kvasir");
        HttpSession session = request.getSession(true);
        session.setAttribute(BasicAuthenticationPageFilter.ATTR_AUTH_TRYING,
            "trying");

        if (errorPage_ != null) {
            try {
                request.getRequestDispatcher(
                    PageUtils.getAbsolutePathname(errorPage_, pageRequest
                        .getMy().getGardRootPage(), pageRequest.getMy()
                        .getPage())).forward(request, response);
            } catch (ServletException ex2) {
                log_.error("Can't forward to error page: " + errorPage_, ex2);
            } catch (IOException ex2) {
                log_.error("Can't forward to error page: " + errorPage_, ex2);
            }
        } else {
            // デフォルトの500エラー画面にするとauthentication headerが
            // 消されるっぽいので、他のハンドラが処理してくれないと
            // Basic認証できないことになることに注意。
            chain.doHandle(request, response, ex, pageRequest);
        }
    }


    /*
     * private scope methods
     */

    private void setAuthenticationHeader(HttpServletResponse response,
        String realm)
    {
        response.setHeader("WWW-Authenticate", "Basic realm=" + realm);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
