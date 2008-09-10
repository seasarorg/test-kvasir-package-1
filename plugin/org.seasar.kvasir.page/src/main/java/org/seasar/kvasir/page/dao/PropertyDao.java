package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Sql;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PropertyDao
{
    @Sql("SELECT pageid,name,value FROM property WHERE pageid=?")
    PropertyDto[] getDtosByPageId(Integer pageId);


    @Sql("pageid=? AND name=?")
    void updateByPageIdAndName(PropertyDto dto, Integer pageId, String name);


    @Sql("DELETE FROM property WHERE pageid=?")
    void deleteByPageId(Integer pageId);


    @Sql("DELETE FROM property WHERE pageid=? AND name=?")
    void deleteByPageIdAndName(Integer pageId, String name);


    void insert(PropertyDto dto);


    @Sql("SELECT COUNT(*) FROM property WHERE pageid=? AND name=?")
    boolean existsPageIdAndName(Integer pageId, String name);
}
