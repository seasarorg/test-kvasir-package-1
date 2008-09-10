package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.User;


public class UserTab extends TabElement
    implements Tab
{
    public static final String NAME_GROUPOFUSER = "groupOfUser";

    public static final String NAME_ROLEOFUSER = "roleOfUser";


    public boolean isDisplayed(Page page)
    {
        return page.getType().equals(User.TYPE);
    }
}
