package org.seasar.kvasir.cms.manage.tab;

import java.util.Locale;

import org.seasar.kvasir.page.Page;


public interface Tab
{
    boolean isDisplayed(Page page);


    String getDisplayName(Locale locale);


    String getName();


    String getPath();


    String getIconPath();


    String getIconLinkPath();
}
