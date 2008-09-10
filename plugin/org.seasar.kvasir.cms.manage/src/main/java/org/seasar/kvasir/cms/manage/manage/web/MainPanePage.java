package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.cms.manage.manage.dto.StatefulTab;
import org.seasar.kvasir.cms.manage.manage.dto.StatefulTabs;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.cms.util.PresentationUtils;
import org.seasar.kvasir.page.Page;


abstract public class MainPanePage extends PageBase
{
    public static final String VARIANT_UNDEFINED = "_undefined";

    /*
     * for presentation tier
     */

    private String onLoadValue_;

    private String transferredOnLoadValue_;

    private StatefulTabs tabs_;

    private String iconURL_;

    private Page[] ancestorNodes_;


    /*
     * protected scope methods
     */

    protected void updateMenu()
    {
        onLoadValue_ = "updateMenu()";
        transferredOnLoadValue_ = onLoadValue_;
    }


    protected void enableLocationBar(boolean enabled)
    {
        if (enabled) {
            iconURL_ = PresentationUtils.getIconURL(getPage(), getPageRequest()
                .getContextPath());
            ancestorNodes_ = prepareAncestorNodes();
        } else {
            iconURL_ = null;
            ancestorNodes_ = null;
        }
    }


    protected void enableTab(String enabledName)
    {
        Page page = getPage();
        Locale locale = getLocale();
        Tab[] tabs = getPlugin().getTabs();
        List<StatefulTab> list = new ArrayList<StatefulTab>(tabs.length);
        for (int i = 0; i < tabs.length; i++) {
            Tab tab = tabs[i];
            if (tab.isDisplayed(page)) {
                list.add(new StatefulTab(tab, locale, tab.getName().equals(
                    enabledName)));
            }
        }
        tabs_ = new StatefulTabs(list.toArray(new StatefulTab[0]));
    }


    Page[] prepareAncestorNodes()
    {
        Page page = getPage();
        if (page.isRoot()) {
            return new Page[0];
        }

        page = page.getParent();
        LinkedList<Page> list = new LinkedList<Page>();
        while (!page.isRoot()) {
            list.addFirst(page);
            page = page.getParent();
        }
        return list.toArray(new Page[0]);
    }


    protected boolean canParent(Page page)
    {
        return true;
    }


    /*
     * for framework / presentation tier
     */

    public String getOnLoadValue()
    {
        return onLoadValue_;
    }


    @Out(SessionScope.class)
    public String getTransferredOnLoadValue()
    {
        return transferredOnLoadValue_;
    }


    @In(SessionScope.class)
    public void setTransferredOnLoadValue(String transferredOnLoadValue)
    {
        onLoadValue_ = transferredOnLoadValue;
        transferredOnLoadValue_ = null;
    }


    public StatefulTabs getTabs()
    {
        return tabs_;
    }


    public String getIconURL()
    {
        return iconURL_;
    }


    public Page[] getAncestorNodes()
    {
        return ancestorNodes_;
    }
}
