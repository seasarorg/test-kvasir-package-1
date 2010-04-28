package org.seasar.kvasir.page.ability.timer.dao.impl;

import org.seasar.cms.beantable.impl.BeantableDaoBase;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDao;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDto;


abstract public class GenericScheduleDao extends BeantableDaoBase<ScheduleDto>
    implements ScheduleDao
{
    @Override
    protected Class<ScheduleDto> getDtoClass()
    {
        return ScheduleDto.class;
    }
}
