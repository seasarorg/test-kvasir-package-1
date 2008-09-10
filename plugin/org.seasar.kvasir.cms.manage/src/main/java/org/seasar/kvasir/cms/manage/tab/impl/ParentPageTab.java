package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;


public class ParentPageTab extends TabElement
    implements Tab
{
    public static final String NAME_LIST = "list";


    public boolean isDisplayed(Page page)
    {
        return true;
    }
}
