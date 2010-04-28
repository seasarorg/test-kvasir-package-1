package org.seasar.kvasir.page.ability.timer;

import java.util.Date;


public class ScheduleMold
{
    private ScheduleStatus status_ = ScheduleStatus.INACTIVE;

    private Date scheduledDate_;

    private String component_;

    private Date beginDate_;

    private Date finishDate_;

    private String errorInformation_;


    public ScheduleMold()
    {
    }


    public ScheduleMold(Date scheduledDate, String component)
    {
        scheduledDate_ = scheduledDate;
        component_ = component;

        status_ = ScheduleStatus.SCHEDULED;
    }


    public ScheduleStatus getStatus()
    {
        return status_;
    }


    public void setStatus(ScheduleStatus status)
    {
        status_ = status;
    }


    public Date getScheduledDate()
    {
        return scheduledDate_;
    }


    public void setScheduledDate(Date executionDate)
    {
        scheduledDate_ = executionDate;
    }


    public String getComponent()
    {
        return component_;
    }


    public void setComponent(String component)
    {
        component_ = component;
    }


    public Date getBeginDate()
    {
        return beginDate_;
    }


    public void setBeginDate(Date beginDate)
    {
        beginDate_ = beginDate;
    }


    public Date getFinishDate()
    {
        return finishDate_;
    }


    public void setFinishDate(Date finishDate)
    {
        finishDate_ = finishDate;
    }


    public String getErrorInformation()
    {
        return errorInformation_;
    }


    public void setErrorInformation(String errorInformation)
    {
        errorInformation_ = errorInformation;
    }
}
