package org.seasar.kvasir.cms.manage.dto;

import java.util.Locale;


public interface PageTree
{
    void setStatus(String pathname, boolean open);


    String render(Locale locale);


    int getHeimId();
}
