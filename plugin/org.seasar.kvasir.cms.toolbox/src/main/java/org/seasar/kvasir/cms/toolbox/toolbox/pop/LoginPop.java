package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.cms.toolbox.toolbox.web.LoginPage;
import org.seasar.kvasir.page.auth.AuthPlugin;


/**
 * ログインフォームを表示するためのPOPです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class LoginPop extends GenericPop
{
    public static final String ID = ToolboxPlugin.ID + ".loginPop";

    private AuthPlugin authPlugin_;


    /*
     * GenericPop
     */

    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        // 未ログインの場合はログインフォームを、ログイン済みの場合は
        // ログアウトリンクを表示するようにする。
        if (authPlugin_.getCurrentActor().isAnonymous()) {
            // 未ログイン。
            popScope.put("note", getNote(context.getRequest()));
        } else {
            // ログイン済み。
            Locale locale = context.getLocale();
            popScope.put(PROP_TITLE, getPlugin().getProperty(
                "pop.loginPop.title.logout", locale));
            popScope.put(PROP_BODY, getElement().getBodyFromResourcePath(
                "pops/loginPop/logout.html", locale));
        }
        return super.render(context, args, popScope);
    }


    private Object getNote(HttpServletRequest request)
    {
        Object note = null;
        if (request != null) {
            HttpSession session = (HttpSession)request.getSession(false);
            if (session != null) {
                note = session.getAttribute(LoginPage.ATTR_NOTE);
                session.removeAttribute(LoginPage.ATTR_NOTE);
            }
        }
        return note;
    }


    /*
     * for framework
     */

    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }
}
