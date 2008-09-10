package org.seasar.kvasir.page.dao.impl;

import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.kvasir.page.dao.PropertyDao;
import org.seasar.kvasir.page.dao.PropertyDto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class GenericPropertyDao extends BeantableDaoBase<PropertyDto>
    implements PropertyDao
{
    @Override
    protected Class<PropertyDto> getDtoClass()
    {
        return PropertyDto.class;
    }
}
