package org.seasar.kvasir.cms.manage;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;
import org.seasar.kvasir.cms.manage.tab.Tab;


public interface ManagePlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.cms.manage";

    String ID_PATH = ID.replace('.', '/');

    String PROP_PAGE_ICON = "page.icon";

    String ATTR_MANAGE_HEIMID = "manage.heimId";


    PageService getPageService(String type);


    Tab[] getTabs();


    String[] getCategoriesOfNewItemMenu();


    NewItemMenuEntry[] getNewItemMenuEntries(String category);
}
