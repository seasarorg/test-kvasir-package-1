package org.seasar.kvasir.page.ability.content.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Sql;


public interface ContentDao
{
    @Sql("SELECT modifydate FROM content WHERE pageid=?")
    Date getModifyDateByPageId(Integer pageId);


    void touchByPageId(Integer pageId);


    @Sql("DELETE FROM content WHERE pageid=?")
    int deleteByPageId(Integer pageId);
}
