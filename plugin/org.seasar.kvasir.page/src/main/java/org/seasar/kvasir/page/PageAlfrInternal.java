package org.seasar.kvasir.page;

import org.seasar.kvasir.page.dao.PageDto;


public interface PageAlfrInternal
{
    int updatePage(Page page, PageDto changeSet);
}
