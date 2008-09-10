package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.cms.PageRequest;
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
public class ConcealPageFilter
    implements PageFilter
{
    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        Page page = pageRequest.getMy().getPage();
        if (page != null && page.isConcealed()) {
            PermissionAbility perm = page.getAbility(PermissionAbility.class);
            User user = authPlugin_.getCurrentActor();
            if (!perm.permits(user, Privilege.ACCESS)) {
                // 基本的に、不可視のページにはアクセスできない。アクセスできるのはそのページに対して
                // 管理者権限を持つものだけ。
                throw new PageNotFoundRuntimeException()
                    .setPathname(pageRequest.getMy().getPathname());
            }
        }

        chain.doFilter(request, response, dispatcher, pageRequest);
    }
}
