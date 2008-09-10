package org.seasar.kvasir.cms.processor.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * 指定されたパスへのリクエストを別のパスに遷移させるPageProcessorです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TransitionPageProcessor
    implements PageProcessor
{
    public static final String PROP_DESTINATION = "destination";

    public static final String PROP_DESTINATION_METHOD = "destination-method";

    private ServletConfig config_;

    private String destination_;

    private boolean redirect_;


    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
        config_ = config;
        destination_ = config_.getInitParameter(PROP_DESTINATION);
        String method = config_.getInitParameter(PROP_DESTINATION_METHOD);
        if (destination_ == null) {
            throw new IllegalArgumentException("Please specify init-property: "
                + PROP_DESTINATION);
        }
        if (method == null || "forward".equals(method)) {
            redirect_ = false;
        } else if ("redirect".equals(method)) {
            redirect_ = true;
        } else {
            throw new IllegalArgumentException("Either forward or redirect"
                + " must be specified for init-property "
                + PROP_DESTINATION_METHOD + ": " + method);
        }
    }


    public void destroy()
    {
        config_ = null;
        destination_ = null;
        redirect_ = false;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        String destination = PageUtils.getAbsolutePathname(destination_,
            pageRequest.getMy().getGardRootPage(), pageRequest.getMy()
                .getPage());
        if (redirect_) {
            response.sendRedirect(response.encodeRedirectURL(ServletUtils
                .getRequestContextPath(request)
                + destination));
        } else {
            request.getRequestDispatcher(destination)
                .forward(request, response);
        }
        return;
    }

    /*
     * for framework
     */
}
