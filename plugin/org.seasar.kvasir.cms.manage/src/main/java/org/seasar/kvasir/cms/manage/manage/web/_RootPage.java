package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;


public class _RootPage
{
    private PageAlfr pageAlfr_;


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public String do_execute()
    {
        if (CmsUtils.getHeimId() == PathId.HEIM_MIDGARD) {
            // サイト管理ツールの初期設定が終了していない場合は設定画面を表示する。
            if (!PropertyUtils.valueOf(pageAlfr_.getRootPage(
                PathId.HEIM_MIDGARD).getAbility(PropertyAbility.class)
                .getProperty(ConfigurePage.PROP_CONFIGURED), false)) {
                return "redirect:/configure.do";
            }
        }
        return "/index.html";
    }


    public String getPathname()
    {
        return "";
    }
}
