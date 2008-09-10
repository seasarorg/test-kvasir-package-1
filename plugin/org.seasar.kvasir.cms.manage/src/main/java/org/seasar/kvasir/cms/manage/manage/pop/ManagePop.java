package org.seasar.kvasir.cms.manage.manage.pop;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.webapp.util.PresentationUtils;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.manage.ManagePlugin;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;


public class ManagePop extends GenericPop
{
    public static final String ID = ManagePlugin.ID + ".managePop";

    public static final String PROP_MANAGEPATHNAME = "managePathname";

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        do {
            User actor = authPlugin_.getCurrentActor();
            if (!actor.isAdministrator()) {
                PageRequest pageRequest = context.getPageRequest();
                if (pageRequest == null) {
                    break;
                }
                Page managePage = pageRequest.getRootPage().getChild(
                    getProperty(popScope, PROP_MANAGEPATHNAME));
                if (managePage == null) {
                    break;
                }
                if (!managePage.getAbility(PermissionAbility.class).permits(
                    actor, Privilege.ACCESS)) {
                    break;
                }
            }

            HttpSession session = context.getRequest().getSession(false);
            boolean finish = (session != null && session
                .getAttribute(PresentationUtils.ATTR_TEMPORAL_SCRIPTS) != null);
            popScope.put("finish", finish);
            return super.render(context, args, popScope);
        } while (false);

        return new RenderedPop(this, "", "");
    }
}
