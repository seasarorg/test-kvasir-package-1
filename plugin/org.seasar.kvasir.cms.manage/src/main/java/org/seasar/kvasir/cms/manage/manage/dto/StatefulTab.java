package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.manage.tab.Tab;


public class StatefulTab
{
    private Tab tab_;

    private Locale locale_;

    private boolean enabled_;


    public StatefulTab(Tab tab, Locale locale)
    {
        this(tab, locale, false);
    }


    public StatefulTab(Tab tab, Locale locale, boolean enabled)
    {
        tab_ = tab;
        locale_ = locale;
        enabled_ = enabled;
    }


    /*
     * public scope methods
     */

    public String getDisplayName()
    {
        return tab_.getDisplayName(locale_);
    }


    public String getPath()
    {
        return tab_.getPath();
    }


    public String getColor()
    {
        return (enabled_) ? "#ffffff" : "#efefef";
    }


    public String getSeparatorColor()
    {
        return (enabled_) ? "#ffffff" : "#c0c0c0";
    }


    public boolean isHasIcon()
    {
        return (tab_.getIconPath() != null);
    }


    public String getIconPath()
    {
        return tab_.getIconPath();
    }


    public String getIconLinkPath()
    {
        String iconLinkPath = tab_.getIconLinkPath();
        if (iconLinkPath != null) {
            return iconLinkPath;
        } else {
            return getPath();
        }
    }
}
