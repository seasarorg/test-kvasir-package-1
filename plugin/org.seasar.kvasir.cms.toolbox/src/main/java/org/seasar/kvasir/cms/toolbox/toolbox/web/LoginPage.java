package org.seasar.kvasir.cms.toolbox.toolbox.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.webapp.util.LocaleUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class LoginPage
{
    public static final String ATTR_NOTE = ToolboxPlugin.ID + ".note";

    private Plugin<?> plugin_;

    private AuthPlugin authPlugin_;

    private CmsPlugin cmsPlugin_;

    private HttpServletRequest httpServletRequest_;

    private Request request_;

    private PageRequest pageRequest_;

    private String name_;

    private String password_;


    public String _post()
    {
        if (name_ != null && password_ != null) {
            // ログイン。
            User user = authPlugin_.authenticate(pageRequest_.getRootPage()
                .getHeimId(), name_, password_);
            if (user == null) {
                // 認証に失敗した。
                HttpSession session = httpServletRequest_.getSession();
                session.setAttribute(ATTR_NOTE, plugin_.getProperty(
                    "login.error.failure", LocaleUtils
                        .findLocale(httpServletRequest_)));
                return "redirect:!" + request_.getPathInfo();
            } else {
                // 認証に成功した。
                cmsPlugin_.login(httpServletRequest_, user);
                return "redirect:!" + request_.getPathInfo();
            }
        } else {
            // ログアウト。
            cmsPlugin_.logout(httpServletRequest_);
            return "redirect:!";
        }
    }


    /*
     * for framework
     */

    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void setCmsPlugin(CmsPlugin cmsPlugin)
    {
        cmsPlugin_ = cmsPlugin;
    }


    public void setHttpServletRequest(HttpServletRequest httpServletRequest)
    {
        httpServletRequest_ = httpServletRequest;
    }


    public void setRequest(Request request)
    {
        request_ = request;
    }


    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public void setPassword(String password)
    {
        password_ = password;
    }
}
