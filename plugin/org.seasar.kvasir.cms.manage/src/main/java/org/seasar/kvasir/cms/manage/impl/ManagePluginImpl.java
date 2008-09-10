package org.seasar.kvasir.cms.manage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.framework.util.ArrayUtil;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.cms.manage.ManagePlugin;
import org.seasar.kvasir.cms.manage.PageService;
import org.seasar.kvasir.cms.manage.manage.service.DirectoryService;
import org.seasar.kvasir.cms.manage.manage.service.GenericPageService;
import org.seasar.kvasir.cms.manage.manage.service.GroupService;
import org.seasar.kvasir.cms.manage.manage.service.RoleService;
import org.seasar.kvasir.cms.manage.manage.service.UserService;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;
import org.seasar.kvasir.cms.manage.menu.impl.ApplicationNewItemMenuEntry;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.type.Directory;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class ManagePluginImpl extends AbstractPlugin<EmptySettings>
    implements ManagePlugin
{
    private MimePlugin mimePlugin_;

    private PagePlugin pagePlugin_;

    private AuthPlugin authPlugin_;

    private Map<String, PageService> pageServiceMap_ = new HashMap<String, PageService>();

    private Tab[] tabs_;

    private String[] categoriesOfNewItemMenu_;

    private Map<String, NewItemMenuEntry[]> newItemMenuEntriesMap_;


    /*
     * constructors
     */

    public ManagePluginImpl()
    {
    }


    /*
     * ManagePlugin
     */

    public PageService getPageService(String type)
    {
        PageService service = pageServiceMap_.get(type);
        if (service == null) {
            service = pageServiceMap_.get(Page.TYPE);
        }
        return service;
    }


    public Tab[] getTabs()
    {
        return tabs_;
    }


    public String[] getCategoriesOfNewItemMenu()
    {
        return categoriesOfNewItemMenu_;
    }


    public NewItemMenuEntry[] getNewItemMenuEntries(String category)
    {
        return newItemMenuEntriesMap_.get(category);
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        preparePageService();
        prepareTabs();
        parepareNewItemMenuEntries();

        return true;
    }


    private void preparePageService()
    {
        GenericPageService pageService = new GenericPageService();
        pageService.setMimePlugin(mimePlugin_);
        pageServiceMap_.put(Page.TYPE, pageService);
        UserService userService = new UserService();
        userService.setMimePlugin(mimePlugin_);
        userService.setAuthPlugin(authPlugin_);
        pageServiceMap_.put(User.TYPE, userService);
        GroupService groupService = new GroupService();
        groupService.setMimePlugin(mimePlugin_);
        pageServiceMap_.put(Group.TYPE, groupService);
        RoleService roleService = new RoleService();
        roleService.setMimePlugin(mimePlugin_);
        pageServiceMap_.put(Role.TYPE, roleService);
        DirectoryService directoryService = new DirectoryService();
        directoryService.setMimePlugin(mimePlugin_);
        pageServiceMap_.put(Directory.TYPE, directoryService);
    }


    private void prepareTabs()
    {
        tabs_ = getExtensionComponents(Tab.class);
    }


    private void parepareNewItemMenuEntries()
    {
        NewItemMenuEntry[] entries = getExtensionComponents(NewItemMenuEntry.class);
        newItemMenuEntriesMap_ = new LinkedHashMap<String, NewItemMenuEntry[]>();
        for (int i = 0; i < entries.length; i++) {
            String cagtegory = entries[i].getCategory();
            NewItemMenuEntry[] es = newItemMenuEntriesMap_.get(cagtegory);
            if (es == null) {
                es = new NewItemMenuEntry[] { entries[i] };
            } else {
                es = (NewItemMenuEntry[])ArrayUtil.add(es, entries[i]);
            }
            newItemMenuEntriesMap_.put(cagtegory, es);
        }

        addApplicationEntries(newItemMenuEntriesMap_);

        // elseを最後に持ってくる。
        NewItemMenuEntry[] es = newItemMenuEntriesMap_
            .get(NewItemMenuEntry.CATEGORY_ELSE);
        if (es != null) {
            newItemMenuEntriesMap_.remove(NewItemMenuEntry.CATEGORY_ELSE);
            newItemMenuEntriesMap_.put(NewItemMenuEntry.CATEGORY_ELSE, es);
        }

        categoriesOfNewItemMenu_ = newItemMenuEntriesMap_.keySet().toArray(
            new String[0]);
    }


    private void addApplicationEntries(Map<String, NewItemMenuEntry[]> map)
    {
        PageGard[] pageGards = pagePlugin_.getPageGards();
        List<NewItemMenuEntry> entryList = new ArrayList<NewItemMenuEntry>(
            pageGards.length);
        for (int i = 0; i < pageGards.length; i++) {
            entryList.add(new ApplicationNewItemMenuEntry(pageGards[i],
                pagePlugin_));
        }
        if (entryList.size() > 0) {
            map.put(ApplicationNewItemMenuEntry.CATEGORY_APPLICATION, entryList
                .toArray(new NewItemMenuEntry[0]));
        }
    }


    protected void doStop()
    {
        mimePlugin_ = null;
        pagePlugin_ = null;
        authPlugin_ = null;

        pageServiceMap_.clear();
        tabs_ = null;
        categoriesOfNewItemMenu_ = null;
        newItemMenuEntriesMap_ = null;
    }


    /*
     * for framework
     */

    public void setMimePlugin(MimePlugin mimePlugin)
    {
        mimePlugin_ = mimePlugin;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }
}
