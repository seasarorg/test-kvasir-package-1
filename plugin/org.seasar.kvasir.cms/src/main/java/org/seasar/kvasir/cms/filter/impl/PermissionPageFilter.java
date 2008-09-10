package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.SecurityRuntimeException;
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
public class PermissionPageFilter
    implements PageFilter
{
    private AuthPlugin authPlugin_;


    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
        authPlugin_ = null;
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        Page page = pageRequest.getMy().getNearestPage();
        PermissionAbility targetPerm = page.getAbility(PermissionAbility.class);
        User user = authPlugin_.getCurrentActor();
        if (!targetPerm.permits(user, Privilege.ACCESS_VIEW)) {
            // 閲覧権限のないページにはアクセスできない。
            throw new SecurityRuntimeException().setPage(
                pageRequest.getMy().getPage()).setUser(user).setPrivilege(
                Privilege.ACCESS_VIEW);
        }

        chain.doFilter(request, response, dispatcher, pageRequest);
    }


    /*
     * for framework
     */

    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }
}
