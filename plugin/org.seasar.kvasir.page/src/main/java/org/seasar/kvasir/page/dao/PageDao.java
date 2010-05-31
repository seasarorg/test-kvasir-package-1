package org.seasar.kvasir.page.dao;

import java.util.List;

import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.PageCondition;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageDao
{
    PageDto getObjectById(Number id);


    PageDto getObjectByPathname(Number heimId, String pathname);


    List<PageDto> getObjectListByIds(Number[] ids);


    List<Object> getObjectList(PageCondition cond);


    List<Number> getIdList(PageCondition cond);


    List<String> getNameListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond);


    List<Number> getIdListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond);


    List<Object> getObjectListByParentPathname(Number heimId,
        String parentPathname, PageCondition cond);


    Number getCountByParentPathname(Number heimId, String parentPathname,
        PageCondition cond);


    Number getCount(PageCondition cond);


    boolean childNameExists(Number heimId, String pathname, String name);


    void insert(PageDto dto);


    int updateById(Number id, PageDto dto);


    int deleteById(Number id);


    void changeLordId(Number heimId, String pathname, Number oldLordId,
        Number newLordId);


    void moveTo(Number heimId, Number fromId, String fromParentPathname,
        String fromName, Number toParentId, String toParentPathname,
        String toName);


    int releasePages(Number ownerUserId);


    <R> R runWithLocking(Number[] ids, Processable<R> processable)
        throws ProcessableRuntimeException, PageNotFoundRuntimeException;
}
