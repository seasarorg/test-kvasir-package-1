package org.seasar.kvasir.page.ability.timer;

import java.util.Calendar;
import java.util.Date;


public class ScheduleMold
{
    private CronFields dayOfWeek_;

    private CronFields year_;

    private CronFields month_;

    private CronFields day_;

    private CronFields hour_;

    private CronFields minute_;

    private String pluginId_;

    private String component_;

    private String parameter_;

    private Boolean enabled_;


    public ScheduleMold()
    {
    }


    public ScheduleMold(CronFields dayOfWeek, CronFields year,
        CronFields month, CronFields day, CronFields hour, CronFields minute,
        String pluginId, String component)
    {
        this(dayOfWeek, year, month, day, hour, minute, pluginId, component,
            null);
    }


    public ScheduleMold(CronFields dayOfWeek, CronFields year,
        CronFields month, CronFields day, CronFields hour, CronFields minute,
        String pluginId, String component, String parameter)
    {
        dayOfWeek_ = dayOfWeek;
        year_ = year;
        month_ = month;
        day_ = day;
        hour_ = hour;
        minute_ = minute;
        pluginId_ = pluginId;
        component_ = component;
        parameter_ = parameter;
    }


    public ScheduleMold(Date date, String pluginId, String component)
    {
        this(date, pluginId, component, null);
    }


    public ScheduleMold(Date date, String pluginId, String component,
        String parameter)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        setYear(CronFields.of(calendar.get(Calendar.YEAR)));
        setMonth(CronFields.of(calendar.get(Calendar.MONTH) + 1));
        setDay(CronFields.of(calendar.get(Calendar.DAY_OF_MONTH)));
        setHour(CronFields.of(calendar.get(Calendar.HOUR_OF_DAY)));
        setMinute(CronFields.of(calendar.get(Calendar.MINUTE)));

        setPluginId(pluginId);
        setComponent(component);
        setParameter(parameter);
    }


    public CronFields getDayOfWeek()
    {
        return dayOfWeek_;
    }


    public void setDayOfWeek(CronFields dayOfWeek)
    {
        dayOfWeek_ = dayOfWeek;
    }


    public CronFields getYear()
    {
        return year_;
    }


    public void setYear(CronFields year)
    {
        year_ = year;
    }


    public CronFields getMonth()
    {
        return month_;
    }


    public void setMonth(CronFields month)
    {
        month_ = month;
    }


    public CronFields getDay()
    {
        return day_;
    }


    public void setDay(CronFields day)
    {
        day_ = day;
    }


    public CronFields getHour()
    {
        return hour_;
    }


    public void setHour(CronFields hour)
    {
        hour_ = hour;
    }


    public CronFields getMinute()
    {
        return minute_;
    }


    public void setMinute(CronFields minute)
    {
        minute_ = minute;
    }


    public String getPluginId()
    {
        return pluginId_;
    }


    public void setPluginId(String pluginId)
    {
        pluginId_ = pluginId;
    }


    public String getComponent()
    {
        return component_;
    }


    public void setComponent(String component)
    {
        component_ = component;
    }


    public String getParameter()
    {
        return parameter_;
    }


    public void setParameter(String parameter)
    {
        parameter_ = parameter;
    }


    public Boolean getEnabled()
    {
        return enabled_;
    }


    public void setEnabled(Boolean enabled)
    {
        enabled_ = enabled;
    }

}
