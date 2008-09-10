package org.seasar.kvasir.page.ability.template.dao.impl;

import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.kvasir.page.ability.template.dao.TemplatesDao;
import org.seasar.kvasir.page.ability.template.dao.TemplatesDto;


abstract public class GenericTemplatesDao extends
    BeantableDaoBase<TemplatesDto>
    implements TemplatesDao
{
    @Override
    protected Class<TemplatesDto> getDtoClass()
    {
        return TemplatesDto.class;
    }
}
