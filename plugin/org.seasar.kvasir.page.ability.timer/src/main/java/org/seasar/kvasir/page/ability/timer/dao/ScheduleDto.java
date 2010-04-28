package org.seasar.kvasir.page.ability.timer.dao;

import java.util.Date;

import org.seasar.dao.annotation.tiger.Bean;
import org.seasar.dao.annotation.tiger.Id;
import org.seasar.dao.annotation.tiger.IdType;
import org.seasar.kvasir.page.ability.timer.ScheduleStatus;


@Bean(table = "schedule", noPersistentProperty = { "statusEnum" })
public class ScheduleDto
{
    private Integer id_;

    private Integer pageId_;

    private Integer status_;

    private Date scheduledDate_;

    private String component_;

    private Date beginDate_;

    private Date finishDate_;

    private String errorInformation_;


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


    public Integer getStatus()
    {
        return status_;
    }


    public ScheduleStatus getStatusEnum()
    {
        return ScheduleStatus.enumOf(status_);
    }


    public void setStatusEnum(ScheduleStatus statusEnum)
    {
        status_ = statusEnum.getId();
    }


    public void setStatus(Integer status)
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
