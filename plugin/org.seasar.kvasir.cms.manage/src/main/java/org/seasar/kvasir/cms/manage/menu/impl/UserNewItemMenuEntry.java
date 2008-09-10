package org.seasar.kvasir.cms.manage.menu.impl;

import org.seasar.kvasir.cms.manage.extension.NewItemMenuEntryElement;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;
import org.seasar.kvasir.page.Page;


public class UserNewItemMenuEntry extends NewItemMenuEntryElement
    implements NewItemMenuEntry
{
    public boolean isDisplayed(Page page)
    {
        return Page.PATHNAME_USERS.equals(page.getLocalPathname());
    }
}
