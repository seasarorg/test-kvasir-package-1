package org.seasar.kvasir.page.ability.timer.impl;

import java.util.Calendar;

import org.seasar.kvasir.page.ability.timer.CronFields;
import org.seasar.kvasir.page.ability.timer.DayOfWeek;
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDto;


public class ScheduleImpl
    implements Schedule
{
    private ScheduleDto dto_;

    private CronFields dayOfWeek_;

    private CronFields year_;

    private CronFields month_;

    private CronFields day_;

    private CronFields hour_;

    private CronFields minute_;


    public ScheduleImpl(ScheduleDto dto)
    {
        dto_ = dto;
    }


    @Override
    public String toString()
    {
        return "ScheduleImpl [dayOfWeek=" + dayOfWeek_ + ", day=" + day_
            + ", dto=" + dto_ + ", hour=" + hour_ + ", minute=" + minute_
            + ", month=" + month_ + ", year=" + year_ + "]";
    }


    public String getComponent()
    {
        return dto_.getComponent();
    }


    public CronFields getDay()
    {
        if (day_ == null) {
            day_ = CronFields.parse(dto_.getDay());
        }
        return day_;
    }


    public boolean isEnabled()
    {
        return dto_.getEnabled().intValue() == ScheduleDto.FALSE ? false : true;
    }


    public CronFields getHour()
    {
        if (hour_ == null) {
            hour_ = CronFields.parse(dto_.getHour());
        }
        return hour_;
    }


    public int getId()
    {
        return dto_.getId();
    }


    public CronFields getMinute()
    {
        if (minute_ == null) {
            minute_ = CronFields.parse(dto_.getMinute());
        }
        return minute_;
    }


    public CronFields getMonth()
    {
        if (month_ == null) {
            month_ = CronFields.parse(dto_.getMonth());
        }
        return month_;
    }


    public CronFields getYear()
    {
        if (year_ == null) {
            year_ = CronFields.parse(dto_.getYear());
        }
        return year_;
    }


    public int getPageId()
    {
        return dto_.getPageId();
    }


    public String getParameter()
    {
        return dto_.getParameter();
    }


    public String getPluginId()
    {
        return dto_.getPluginId();
    }


    public CronFields getDayOfWeek()
    {
        if (dayOfWeek_ == null) {
            dayOfWeek_ = CronFields.parse(dto_.getDayOfWeek());
        }
        return dayOfWeek_;
    }


    public boolean isMatched(Calendar calendar)
    {
        if (calendar == null) {
            return false;
        }

        if (!getDayOfWeek().isMatched(
            DayOfWeek.enumOfCalendarDayOfWeek(calendar
                .get(Calendar.DAY_OF_WEEK)))) {
            return false;
        }

        if (!getMonth().isMatched(calendar.get(Calendar.MONTH) + 1)) {
            return false;
        }

        if (!getDay().isMatched(calendar.get(Calendar.DAY_OF_MONTH))) {
            return false;
        }

        if (!getHour().isMatched(calendar.get(Calendar.HOUR_OF_DAY))) {
            return false;
        }

        if (!getMinute().isMatched(calendar.get(Calendar.MINUTE))) {
            return false;
        }

        return true;
    }
}
