package org.seasar.kvasir.page.ability.timer;

public enum ScheduleStatus implements ScheduleStatusConstants
{
    INACTIVE(ID_INACTIVE), SCHEDULED(ID_SCHEDULED), RUNNING(ID_RUNNING), FINISHED(
        ID_FINISHED), CANCELLED(ID_CANCELLED);

    public static ScheduleStatus enumOf(Integer id)
    {
        for (ScheduleStatus enm : values()) {
            if (enm.getId().equals(id)) {
                return enm;
            }
        }
        return null;
    }


    private Integer id_;


    private ScheduleStatus(Integer id)
    {
        id_ = id;
    }


    public Integer getId()
    {
        return id_;
    }
}
