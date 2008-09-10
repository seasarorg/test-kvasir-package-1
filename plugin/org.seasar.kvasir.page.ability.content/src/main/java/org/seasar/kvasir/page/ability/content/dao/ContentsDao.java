package org.seasar.kvasir.page.ability.content.dao;

import java.util.Date;
import java.util.Locale;

import org.seasar.dao.annotation.tiger.Sql;


public interface ContentsDao
{
    @Sql("SELECT * FROM contents WHERE pageid=:pageId AND latest<>0 AND variant IN (??) ORDER BY length(variant) DESC LIMIT 1")
    ContentsDto getObjectByPageIdAndLocale(Integer pageId, Locale locale);


    @Sql("DELETE FROM contents WHERE pageid=? AND variant=?")
    int deleteByPageIdAndVariant(Integer pageId, String variant);


    @Sql("DELETE FROM contents WHERE pageid=?")
    int deleteByPageId(Integer pageId);


    @Sql("DELETE FROM contents WHERE pageid=? AND variant=? AND revisionnumber=?")
    void deleteByPageIdAndVariantAndRevisionNumber(Integer pageId,
        String variant, int revisionNumber);


    @Sql("SELECT * FROM contents WHERE pageid=? AND variant=? AND revisionnumber=?")
    ContentsDto getObjectByPageIdAndVariantAndRevisionNumber(Integer pageId,
        String variant, Integer revisionNumber);


    @Sql("SELECT COALESCE(min(revisionnumber), 0) FROM contents WHERE pageid=? AND variant=? AND revisionnumber<>0")
    Number getEarliestRevisionNumberByPageIdAndVariant(Integer pageId,
        String variant);


    @Sql("SELECT revisionnumber FROM contents WHERE pageid=? AND variant=? AND ? BETWEEN createdate AND modifydate")
    Number getRevisionNumberByPageIdAndVariantAndDate(Integer pageId,
        String variant, Date date);


    @Sql("SELECT * FROM contents WHERE pageid=? AND variant=? AND latest<>0")
    ContentsDto getLatestObjectByPageIdAndVariant(Integer pageId, String variant);


    void insert(ContentsDto dto);


    @Sql("SELECT DISTINCT variant FROM contents WHERE pageid=?")
    String[] getVariantsByPageId(Integer pageId);


    @Sql("id=?")
    int updateById(ContentsDto dto, Integer id);


    @Sql("DELETE FROM contents WHERE id=?")
    int deleteById(Integer id);


    @Sql("SELECT id FROM contents WHERE pageid=? AND variant=?")
    Number[] getIdsByPageIdAndVariant(Integer pageId, String variant);


    @Sql("SELECT id FROM contents WHERE pageid=?")
    Number[] getIdsByPageId(Integer pageId);
}
