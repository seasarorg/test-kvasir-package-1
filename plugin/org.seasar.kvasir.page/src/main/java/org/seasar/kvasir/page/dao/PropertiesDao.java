package org.seasar.kvasir.page.dao;

import org.seasar.dao.annotation.tiger.Sql;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PropertiesDao
{
    @Sql("SELECT body FROM properties WHERE pageid=? AND variant=?")
    String getBodyByPageIdAndVariant(Integer pageId, String variant);


    PropertiesDto[] getDtosByPageIdAndVariants(Integer pageId, String[] variants);


    @Sql("pageid=? AND variant=?")
    void updateByPageIdAndVariant(PropertiesDto dto, Integer pageId,
        String variant);


    @Sql("DELETE FROM properties WHERE pageid=?")
    void deleteByPageId(Integer pageId);


    @Sql("DELETE FROM properties WHERE pageid=? AND variant=?")
    void deleteByPageIdAndVariant(Integer pageId, String variant);


    void insert(PropertiesDto dto);


    @Sql("SELECT COUNT(*) FROM properties WHERE pageid=? AND variant=?")
    boolean existsByPageIdAndVariant(Integer pageId, String variant);


    @Sql("SELECT variant FROM properties WHERE pageid=?")
    String[] getVariantsByPageId(Integer pageId);
}
