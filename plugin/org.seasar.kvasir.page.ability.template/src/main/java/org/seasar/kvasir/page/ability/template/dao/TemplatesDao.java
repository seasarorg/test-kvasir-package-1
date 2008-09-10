package org.seasar.kvasir.page.ability.template.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Sql;


public interface TemplatesDao
{
    @Sql("SELECT * FROM templates WHERE pageid=? AND variant=?")
    TemplatesDto getObjectByPageIdAndVariant(Integer pageId, String variant);


    void insert(TemplatesDto dto);


    @Sql("pageid=? AND variant=?")
    void updateByPageIdAndVariant(TemplatesDto dto, Integer pageId,
        String variant);


    @Sql("SELECT COUNT(*) FROM templates WHERE pageid=? AND variant=?")
    boolean existsByPageIdAndVariant(Integer pageId, String variant);


    @Sql("DELETE FROM templates WHERE pageid=? AND variant=?")
    void deleteByPageIdAndVariant(Integer pageId, String variant);


    @Sql("DELETE FROM templates WHERE pageid=?")
    void deleteByPageId(Integer pageId);


    @Sql("SELECT modifydate FROM templates WHERE pageid=? AND variant=?")
    Date getModifyDateByPageIdAndVariant(Integer pageId, String variant);


    @Sql("SELECT variant FROM templates WHERE pageid=?")
    String[] getVariantsByPageId(Integer pageId);
}
