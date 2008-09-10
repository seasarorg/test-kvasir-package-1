package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.Role;


public class RoleTab extends TabElement
    implements Tab
{
    public static final String NAME_MEMBEROFROLE = "memberOfRole";


    public boolean isDisplayed(Page page)
    {
        return (page.getType().equals(Role.TYPE)
            && page.getId() != Page.ID_ALL_ROLE && page.getId() != Page.ID_OWNER_ROLE);
    }
}
