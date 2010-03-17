package org.seasar.kvasir.cms.kdiary.kdiary.pop;

import java.util.Map;

import org.seasar.kvasir.cms.kdiary.KdiaryPlugin;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;


public class NaviPop extends GenericPop
{
    public static final String ID = KdiaryPlugin.ID + ".naviPop";

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        popScope.put("indexPage", context.getThat().getGardRootPage()
            .getAbility(PropertyAbility.class).getProperty("indexPage"));

        boolean showRecentButton = false;
        boolean showEditButton = false;
        boolean showAppendButton = false;
        boolean showConfigButton = false;

        String pathname = context.getThat().getLocalPathname();
        if (pathname.equals("")) {
            showAppendButton = true;
        } else if (pathname.startsWith("/action/")) {
            showRecentButton = true;
            showAppendButton = true;
            showConfigButton = true;
        } else {
            Page page = context.getThat().getPage();
            if (page != null && page.getType().equals(Page.TYPE)) {
                showRecentButton = true;
                showEditButton = true;
                String name = page.getName();
                int dot = name.lastIndexOf('.');
                if (dot >= 0) {
                    name = name.substring(0, dot);
                }
                popScope.put("date", page.getParent().getName() + name);
            } else {
                showRecentButton = true;
                showAppendButton = true;
            }
        }

        if (!context.getThat().getNearestPage().getAbility(
            PermissionAbility.class).permits(authPlugin_.getCurrentActor(),
            Privilege.ACCESS)) {
            showEditButton = false;
            showAppendButton = false;
            showConfigButton = false;
        }

        popScope.put("showRecentButton", showRecentButton);
        popScope.put("showEditButton", showEditButton);
        popScope.put("showAppendButton", showAppendButton);
        popScope.put("showConfigButton", showConfigButton);

        return super.render(context, args, popScope);
    }
}
