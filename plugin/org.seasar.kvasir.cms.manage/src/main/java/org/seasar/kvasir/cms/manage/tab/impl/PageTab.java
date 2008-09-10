package org.seasar.kvasir.cms.manage.tab.impl;

import org.seasar.kvasir.cms.manage.extension.TabElement;
import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.page.Page;


public class PageTab extends TabElement
    implements Tab
{
    public static final String NAME_VIEW = "view";

    public static final String NAME_EDIT = "edit";

    public static final String NAME_PROPERTY = "property";

    public static final String NAME_PERMISSION = "permission";

    public static final String NAME_CONTENT = "content";

    public static final String NAME_TEMPLATE = "template";


    public boolean isDisplayed(Page page)
    {
        return true;
    }
}
