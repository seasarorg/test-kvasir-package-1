package org.seasar.kvasir.page.ability.timer.impl;

import java.util.Date;

import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.ScheduleStatus;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDto;


public class ScheduleImpl
    implements Schedule
{
    private ScheduleDto dto_;


    public ScheduleImpl(ScheduleDto dto)
    {
        dto_ = dto;
    }


    public Date getBeginDate()
    {
        return dto_.getBeginDate();
    }


    public String getComponent()
    {
        return dto_.getComponent();
    }


    public String getErrorInformation()
    {
        return dto_.getErrorInformation();
    }


    public Date getFinishDate()
    {
        return dto_.getFinishDate();
    }


    public int getId()
    {
        return dto_.getId();
    }


    public Date getScheduledDate()
    {
        return dto_.getScheduledDate();
    }


    public ScheduleStatus getStatus()
    {
        return dto_.getStatusEnum();
    }


    public boolean isSucceed()
    {
        return dto_.getErrorInformation() == null
            || dto_.getErrorInformation().length() == 0;
    }
}
