package org.seasar.kvasir.page.ability.template.dao;

import org.seasar.dao.annotation.tiger.Sql;


public interface TemplateDao
{
    @Sql("DELETE FROM template WHERE pageid=?")
    void deleteByPageId(Integer pageId);


    @Sql("SELECT * FROM template WHERE pageid=?")
    TemplateDto getObjectByPageId(Integer pageId);


    @Sql("pageid=?")
    void updateByPageId(TemplateDto dto, Integer pageId);


    @Sql("SELECT COUNT(*) FROM template WHERE pageid=?")
    boolean existsByPageId(Integer pageId);


    void insert(TemplateDto dto);
}
