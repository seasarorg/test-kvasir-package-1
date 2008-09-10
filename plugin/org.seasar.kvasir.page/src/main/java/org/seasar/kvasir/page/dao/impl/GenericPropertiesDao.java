package org.seasar.kvasir.page.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.cms.database.SQLRuntimeException;
import org.seasar.dao.annotation.tiger.Sql;
import org.seasar.kvasir.page.dao.PropertiesDao;
import org.seasar.kvasir.page.dao.PropertiesDto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class GenericPropertiesDao extends
    BeantableDaoBase<PropertiesDto>
    implements PropertiesDao
{
    @Override
    protected Class<PropertiesDto> getDtoClass()
    {
        return PropertiesDto.class;
    }


    @Sql("SELECT pageid,variant,body FROM properties WHERE pageid=? AND variant IN (??)")
    @SuppressWarnings("unchecked")
    public PropertiesDto[] getDtosByPageIdAndVariants(Integer pageId,
        String[] variants)
    {
        if (variants.length == 0) {
            return new PropertiesDto[0];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < variants.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
        }

        String template = constructPair("getObjectListByPageIdAndVariants",
            new String[] { sb.toString() }, null, variants).getTemplate();

        Connection con = null;
        try {
            con = getConnection();
            QueryRunner run = new QueryRunner();
            return (PropertiesDto[])((List<PropertiesDto>)run.query(con,
                template, variants, beantableListHandler_))
                .toArray(new PropertiesDto[0]);
        } catch (SQLException ex) {
            throw new SQLRuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}
