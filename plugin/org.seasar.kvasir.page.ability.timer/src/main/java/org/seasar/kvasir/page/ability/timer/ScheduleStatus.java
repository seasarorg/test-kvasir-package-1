package org.seasar.kvasir.page.ability.timer;

public enum ScheduleStatus
{
    INACTIVE(0), SCHEDULED(1), RUNNING(2), FINISHED(3), CANCELLED(4);

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
