package org.seasar.kvasir.page.ability.template.dao.impl;

import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.kvasir.page.ability.template.dao.TemplateDao;
import org.seasar.kvasir.page.ability.template.dao.TemplateDto;


abstract public class GenericTemplateDao extends BeantableDaoBase<TemplateDto>
    implements TemplateDao
{
    @Override
    protected Class<TemplateDto> getDtoClass()
    {
        return TemplateDto.class;
    }
}
