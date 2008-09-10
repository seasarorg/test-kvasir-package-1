package org.seasar.kvasir.page.ability.content.dao.impl;

import java.sql.SQLException;
import java.util.Date;

import org.seasar.cms.beantable.Formula;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.dao.annotation.tiger.Sql;
import org.seasar.kvasir.page.ability.content.dao.ContentDao;
import org.seasar.kvasir.page.ability.content.dao.ContentDto;


abstract public class GenericContentDao extends BeantableDaoBase<ContentDto>
    implements ContentDao
{
    @Override
    protected Class<ContentDto> getDtoClass()
    {
        return ContentDto.class;
    }


    @Sql("pageid=?")
    public void touchByPageId(Integer pageId)
    {
        try {
            Formula where = new Formula(getQuery("touchByPageId"));
            where.setObject(1, pageId);

            Date now = new Date();
            ContentDto changeSet = new ContentDto();
            changeSet.setModifyDate(now);
            if (beantable_.updateColumns(changeSet, where) == 0) {
                beantable_.insertColumn(new ContentDto(pageId, now));
            }
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        }
    }
}
