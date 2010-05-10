package org.seasar.kvasir.page.ability.timer.dao;

import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;


@Bean(table = "schedule")
public class ScheduleDto
{
    public static final int TRUE = 1;

    public static final int FALSE = 0;

    private Integer id_;

    private Integer pageId_;

    private String dayOfWeek_;

    private String year_;

    private String month_;

    private String day_;

    private String hour_;

    private String minute_;

    private String pluginId_;

    private String component_;

    private String parameter_;

    private Integer enabled_;


    public ScheduleDto()
    {
    }


    public Integer getId()
    {
        return id_;
    }


    @Id(IdType.IDENTITY)
    public void setId(Integer id)
    {
        id_ = id;
    }


    public Integer getPageId()
    {
        return pageId_;
    }


    public void setPageId(Integer pageId)
    {
        pageId_ = pageId;
    }


    public String getDayOfWeek()
    {
        return dayOfWeek_;
    }


    public void setDayOfWeek(String dayOfWeek)
    {
        dayOfWeek_ = dayOfWeek;
    }


    public String getYear()
    {
        return year_;
    }


    public void setYear(String year)
    {
        year_ = year;
    }


    public String getMonth()
    {
        return month_;
    }


    public void setMonth(String month)
    {
        month_ = month;
    }


    public String getDay()
    {
        return day_;
    }


    public void setDay(String day)
    {
        day_ = day;
    }


    public String getHour()
    {
        return hour_;
    }


    public void setHour(String hour)
    {
        hour_ = hour;
    }


    public String getMinute()
    {
        return minute_;
    }


    public void setMinute(String minute)
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


    public Integer getEnabled()
    {
        return enabled_;
    }


    public void setEnabled(Integer enabled)
    {
        enabled_ = enabled;
    }
}
