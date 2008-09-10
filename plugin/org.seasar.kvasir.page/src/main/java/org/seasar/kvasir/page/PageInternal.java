package org.seasar.kvasir.page;

import org.seasar.kvasir.page.dao.PageDto;


/**
 * @author YOKOTA Takehiko
 */
public interface PageInternal
{
    PageDto getDto();


    void setDto(PageDto dto);
}
