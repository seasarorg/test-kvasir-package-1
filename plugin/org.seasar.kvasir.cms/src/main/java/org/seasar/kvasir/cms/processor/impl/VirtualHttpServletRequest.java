package org.seasar.kvasir.cms.processor.impl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class VirtualHttpServletRequest extends HttpServletRequestWrapper
{
    public static final int LENGTH_WHOLE = -1;

    private static final String SUFFIX_WITHPATHINFO = "/*";

    private ServletContext context_;

    private String contextPath_;

    private String servletPath_;

    private String pathInfo_;

    private boolean activated_;

    private String includedContextPath_;

    private String includedServletPath_;

    private String includedPathInfo_;


    public VirtualHttpServletRequest(VirtualServletContext context,
        HttpServletRequest request, String lordPathname, String localPathname,
        String urlPattern)
    {
        super(request);
        context_ = context;

        String path = localPathname
            + (ServletUtils.getNativePath(request).endsWith("/") ? "/" : "");
        String servletPath;
        String pathInfo;
        int servletPathLength = getServletPathLength(urlPattern);
        if (servletPathLength == LENGTH_WHOLE) {
            servletPath = path;
            pathInfo = null;
        } else {
            servletPath = path.substring(0, servletPathLength);
            pathInfo = path.substring(servletPathLength);
            if (pathInfo.length() == 0) {
                pathInfo = null;
            }
        }
        if (ServletUtils.isIncluded(request)) {
            contextPath_ = request.getContextPath();
            servletPath_ = request.getServletPath();
            pathInfo_ = request.getPathInfo();

            includedContextPath_ = ServletUtils.getIncludeContextPath(request)
                + lordPathname;

            includedServletPath_ = servletPath;
            includedPathInfo_ = pathInfo;
        } else {
            contextPath_ = request.getContextPath() + lordPathname;

            servletPath_ = servletPath;
            pathInfo_ = pathInfo;

            includedContextPath_ = null;
            includedServletPath_ = null;
            includedPathInfo_ = null;
        }

        activated_ = true;
    }


    /*
     * for unit test
     */
    VirtualHttpServletRequest(HttpServletRequest request)
    {
        super(request);
    }


    int getServletPathLength(String urlPattern)
    {
        if (urlPattern == null) {
            return 0;
        } else if (urlPattern.endsWith(SUFFIX_WITHPATHINFO)) {
            return urlPattern.length() - SUFFIX_WITHPATHINFO.length();
        } else {
            return LENGTH_WHOLE;
        }
    }


    /*
     * public scope methodsn
     */

    public boolean isActivated()
    {
        return activated_;
    }


    public void setActivated(boolean activated)
    {
        activated_ = activated;
    }


    /*
     * HttpServletRequest
     */

    public Object getAttribute(String name)
    {
        if (activated_) {
            if (ServletUtils.ATTR_INCLUDE_CONTEXT_PATH.equals(name)) {
                return includedContextPath_;
            } else if (ServletUtils.ATTR_INCLUDE_SERVLET_PATH.equals(name)) {
                return includedServletPath_;
            } else if (ServletUtils.ATTR_INCLUDE_PATH_INFO.equals(name)) {
                return includedPathInfo_;
            }
        }
        return super.getAttribute(name);
    }


    public String getContextPath()
    {
        if (activated_) {
            return contextPath_;
        } else {
            return super.getContextPath();
        }
    }


    public String getPathInfo()
    {
        if (activated_) {
            return pathInfo_;
        } else {
            return super.getPathInfo();
        }
    }


    public String getPathTranslated()
    {
        return null;
    }


    public String getServletPath()
    {
        if (activated_) {
            return servletPath_;
        } else {
            return super.getServletPath();
        }
    }


    /**
     * @deprecated
     */
    public String getRealPath(String arg0)
    {
        return null;
    }


    public RequestDispatcher getRequestDispatcher(String path)
    {
        if (activated_) {
            if (path.length() == 0 || path.startsWith("/")
                || path.startsWith("!")) {
                ;
            } else {
                path = servletPath_ + (servletPath_.endsWith("/") ? "" : "/")
                    + path;
            }
            return context_.getRequestDispatcher(path);
        } else {
            return super.getRequestDispatcher(path);
        }
    }
}
