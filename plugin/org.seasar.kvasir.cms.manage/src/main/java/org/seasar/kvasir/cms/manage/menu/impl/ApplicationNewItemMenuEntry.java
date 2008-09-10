package org.seasar.kvasir.cms.manage.menu.impl;

import java.util.Locale;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.gard.PageGard;


public class ApplicationNewItemMenuEntry
    implements NewItemMenuEntry
{
    private static final String PATH = "@/new-application.do";

    public static final String CATEGORY_APPLICATION = "application";

    private PageGard pageGard_;

    private PagePlugin pagePlugin_;

    private Plugin<?> plugin_;

    private String displayName_;


    public ApplicationNewItemMenuEntry(PageGard pageGard, PagePlugin pagePlugin)
    {
        pageGard_ = pageGard;
        pagePlugin_ = pagePlugin;
        plugin_ = pageGard.getPlugin();
        displayName_ = pageGard.getDisplayName();
        if (displayName_ == null) {
            displayName_ = pageGard.getId();
        }
    }


    public boolean isDisplayed(Page page)
    {
        if (pageGard_.isSingleton()) {
            return (page.isRoot() && !pagePlugin_.isAlreadyIntalled(pageGard_,
                page.getHeimId()));
        } else {
            return true;
        }
    }


    public String getDisplayName(Locale locale)
    {
        return plugin_.resolveString(displayName_, locale);
    }


    public String getName()
    {
        return pageGard_.getId();
    }


    public String getPath()
    {
        return PATH;
    }


    public String getParameter()
    {
        return "?gardId=" + pageGard_.getFullId();
    }


    public String getCategory()
    {
        return CATEGORY_APPLICATION;
    }
}
