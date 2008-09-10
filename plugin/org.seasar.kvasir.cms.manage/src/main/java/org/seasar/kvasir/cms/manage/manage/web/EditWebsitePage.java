package org.seasar.kvasir.cms.manage.manage.web;

import static org.seasar.kvasir.base.webapp.WebappPlugin.PATH_JS;

import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.base.webapp.util.PresentationUtils;


public class EditWebsitePage extends PageBase
{
    private static final String PATH_PROTOTYPE_JS = PATH_JS
        + "/prototype/prototype.js";

    private static final String PATH_SCRIPTACULOUS_JS = PATH_JS
        + "/scriptaculous/scriptaculous.js";

    private static final String PATH_LITBOX_JS = PATH_JS + "/litbox/litbox.js";

    private static final String PATH_LITBOX_CSS = PATH_JS
        + "/litbox/litbox.css";

    private static final String PATH_DOCK_JS = PATH_JS + "/dock/dock.js";

    private boolean finish_;


    public void setFinish(boolean finish)
    {
        finish_ = finish;
    }


    public Object do_execute()
    {
        return getRedirection("!" + getPathname());
    }


    @Out(scopeClass = SessionScope.class, name = PresentationUtils.ATTR_TEMPORAL_STYLES)
    public String[] getStyles()
    {
        if (finish_) {
            return null;
        }

        String managePathname = getPageRequest().getMy().getGardRootPage()
            .getPathname();
        return new String[] { PATH_LITBOX_CSS,
            managePathname + "/css/tabified.css",
            managePathname + "/css/layout.css", };
    }


    @Out(scopeClass = SessionScope.class, name = PresentationUtils.ATTR_TEMPORAL_SCRIPTS)
    public String[] getScripts()
    {
        if (finish_) {
            return null;
        }

        String managePathname = getPageRequest().getMy().getGardRootPage()
            .getPathname();
        return new String[] { PATH_PROTOTYPE_JS, PATH_SCRIPTACULOUS_JS,
            PATH_LITBOX_JS, PATH_DOCK_JS, managePathname + "/js/tabified.js",
            managePathname + "/js/layout.js", };
    }
}
