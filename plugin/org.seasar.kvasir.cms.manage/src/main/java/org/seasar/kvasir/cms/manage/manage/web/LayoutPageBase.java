package org.seasar.kvasir.cms.manage.manage.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.Page;


abstract public class LayoutPageBase extends MainPanePage
{
    private static final String DOMAIN_DELIM = "://";

    protected HttpServletRequest httpRequest_;

    protected HttpServletResponse httpResponse_;

    protected String start_;


    public void setHttpRequest(HttpServletRequest httpRequest)
    {
        httpRequest_ = httpRequest;
    }


    public void setHttpResponse(HttpServletResponse httpResponse)
    {
        httpResponse_ = httpResponse;
    }


    public void setStart(String start)
    {
        start_ = start;
    }


    public String findStartPathname()
    {
        String pathname = getStartPathname();
        if (pathname == null) {
            return "";
        } else {
            return pathname;
        }
    }


    public String getStartPathname()
    {
        if (start_ == null) {
            return null;
        } else if (start_.length() == 0 || start_.startsWith("/")) {
            return start_;
        }

        // スキームとドメイン（とポート番号）を捨てる。
        int delim = start_.indexOf(DOMAIN_DELIM);
        if (delim < 0) {
            return null;
        }
        int slash = start_.indexOf('/', delim + DOMAIN_DELIM.length());
        if (slash < 0) {
            return null;
        }
        String startURI = start_.substring(slash);
        String contextPath = CmsUtils.getOriginalContextPath(httpRequest_);
        if (!startURI.startsWith(contextPath)) {
            return null;
        }
        String startPathname;
        try {
            startPathname = URLDecoder.decode(startURI.substring(contextPath
                .length()), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Can't happen!");
        }
        int no = startPathname.indexOf('#');
        if (no >= 0) {
            startPathname = startPathname.substring(0, no);
        }
        if (startPathname.endsWith("/")) {
            startPathname = startPathname.substring(0,
                startPathname.length() - 1);
        }
        return startPathname;
    }


    public Page getStartPage()
    {
        String pathname = getStartPathname();
        if (pathname == null) {
            return null;
        } else {
            return getPageAlfr().getPage(getCurrentHeimId(), pathname);
        }
    }
}
