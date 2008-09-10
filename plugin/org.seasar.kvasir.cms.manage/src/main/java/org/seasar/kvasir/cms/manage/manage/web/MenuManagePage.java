package org.seasar.kvasir.cms.manage.manage.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.cms.manage.dto.PageTree;
import org.seasar.kvasir.cms.manage.manage.dto.PageTreeImpl;
import org.seasar.kvasir.webapp.util.ServletUtils;


public class MenuManagePage extends PageBase
{
    /*
     * set by framework
     */

    private HttpServletRequest request_;

    private HttpServletResponse response_;

    private String status_;

    /*
     * for presentation tier
     */

    private String pageTree_;


    /*
     * public scope methods
     */

    public String do_execute()
    {
        pageTree_ = preparePageTree();

        // IEではリフレッシュ要求が来てもキャッシュのせいかページが更新され
        // ないことがあるのでno cacheにしておく。
        ServletUtils.setNoCache(response_);

        return "/menu-manage.html";
    }


    /*
     * private scope methods
     */

    private String preparePageTree()
    {
        HttpSession session = request_.getSession();
        synchronized (session.getId().intern()) {
            Object pageTreeObj = session.getAttribute("pageTree");
            PageTree pageTree = null;
            if (pageTreeObj instanceof PageTree) {
                pageTree = (PageTree)pageTreeObj;
                if (pageTree.getHeimId() != getCurrentHeimId()) {
                    pageTree = null;
                }
            }
            if (pageTree == null) {
                String topURI = getPageRequest().getContextPath()
                    + getTopPathname();

                pageTree = new PageTreeImpl(topURI, getPageAlfr().getRootPage(
                    getCurrentHeimId()));
                session.setAttribute("pageTree", pageTree);
            }
            if (status_ != null) {
                pageTree.setStatus(getPathname(), "open".equals(status_));
            }
            return pageTree.render(getPageRequest().getLocale());
        }
    }


    /*
     * for framework
     */

    public void setRequest(HttpServletRequest request)
    {
        request_ = request;
    }


    public void setResponse(HttpServletResponse response)
    {
        response_ = response;
    }


    public void setStatus(String status)
    {
        status_ = status;
    }


    /*
     * for presentation tier
     */

    public String getPageTree()
    {
        return pageTree_;
    }
}
