package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;


public class NodePageTab extends TabElement
    implements Tab
{
    public boolean isDisplayed(Page page)
    {
        return page.isNode();
    }
}
