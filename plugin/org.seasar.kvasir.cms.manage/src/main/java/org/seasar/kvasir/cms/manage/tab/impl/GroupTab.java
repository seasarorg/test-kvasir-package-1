package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.Group;


public class GroupTab extends TabElement
    implements Tab
{
    public static final String NAME_USEROFGROUP = "userOfGroup";

    public static final String NAME_ROLEOFGROUP = "roleOfGroup";


    public boolean isDisplayed(Page page)
    {
        return (page.getType().equals(Group.TYPE) && page.getId() != Page.ID_ALL_GROUP);
    }
}
