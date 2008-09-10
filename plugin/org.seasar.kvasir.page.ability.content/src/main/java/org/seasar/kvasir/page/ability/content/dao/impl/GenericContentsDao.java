package org.seasar.kvasir.page.ability.content.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.seasar.cms.beantable.Pair;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.kvasir.page.ability.content.dao.ContentsDao;
import org.seasar.kvasir.page.ability.content.dao.ContentsDto;
import org.seasar.kvasir.util.LocaleUtils;


abstract public class GenericContentsDao extends BeantableDaoBase<ContentsDto>
    implements ContentsDao
{
    @Override
    protected Class<ContentsDto> getDtoClass()
    {
        return ContentsDto.class;
    }


    public ContentsDto getObjectByPageIdAndLocale(Integer pageId, Locale locale)
    {
        String[] suffixes = LocaleUtils.getSuffixes(locale, true);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < suffixes.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
        }

        Map<String, Object> namedParamMap = new HashMap<String, Object>();
        namedParamMap.put("pageId", pageId);
        Pair pair = constructPair("getObjectByPageIdAndLocale",
            new String[] { sb.toString() }, namedParamMap, suffixes);

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = new QueryRunner();
            return (ContentsDto)run.query(con, pair.getTemplate(), pair
                .getParameters(), beantableHandler_);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
