package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ActorBindingPageFilter
    implements PageFilter
{
    private PagePlugin pagePlugin_;

    private AuthPlugin authPlugin_;

    private CmsPlugin cmsPlugin_;

    private PageAlfr pageAlfr_;


    public void init(FilterConfig config)
    {
        pageAlfr_ = pagePlugin_.getPageAlfr();
    }


    public void destroy()
    {
        pagePlugin_ = null;
        authPlugin_ = null;
        cmsPlugin_ = null;

        pageAlfr_ = null;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        User user = cmsPlugin_.getUser(request);
        if (user == null) {
            user = (User)pageAlfr_.getPage(User.class, Page.ID_ANONYMOUS_USER);
        }
        authPlugin_.actAs(user);
        try {
            chain.doFilter(request, response, dispatcher, pageRequest);
        } finally {
            authPlugin_.actAs(null);
        }
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void setCmsPlugin(CmsPlugin webappPlugin)
    {
        cmsPlugin_ = webappPlugin;
    }
}
