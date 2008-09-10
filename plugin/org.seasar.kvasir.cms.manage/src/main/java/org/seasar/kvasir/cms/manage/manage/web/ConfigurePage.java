package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.cms.manage.ManagePlugin;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class ConfigurePage extends PageBase
{
    public static final String PROP_CONFIGURED = ManagePlugin.ID
        + ".configured";

    private AuthPlugin authPlugin_;

    private String password_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    public void setPassword(String password)
    {
        password_ = password.trim();
    }


    public String do_execute()
    {
        if (!canExecute()) {
            return "redirect:";
        }

        return "/configure.html";
    }


    boolean canExecute()
    {
        return (CmsUtils.getHeimId() == PathId.HEIM_MIDGARD && !PropertyUtils
            .valueOf(getPageAlfr().getRootPage(PathId.HEIM_MIDGARD).getAbility(
                PropertyAbility.class).getProperty(PROP_CONFIGURED), false));
    }


    public String do_apply()
    {
        if (!canExecute()) {
            return "redirect:";
        }

        if (password_ == null || password_.length() == 0) {
            // 空のパスワードは設定できないようにする。
            setNotes(new Notes().add(new Note(
                "app.error.userService.passwordIsEmpty")));
            return "/configure.html";
        }

        authPlugin_.getDefaultAuthSystem().changePassword(
            getPageAlfr().getAdministrator(), password_);

        return "redirect:/configure.succeed.do";
    }


    public String do_succeed()
    {
        if (!canExecute()) {
            return "redirect:";
        }

        getPageAlfr().getRootPage(PathId.HEIM_MIDGARD).getAbility(
            PropertyAbility.class).setProperty(PROP_CONFIGURED,
            String.valueOf(true));

        return "/configure.succeed.html";
    }
}
