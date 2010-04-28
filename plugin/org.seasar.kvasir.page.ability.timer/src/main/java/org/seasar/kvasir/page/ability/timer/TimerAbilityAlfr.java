package org.seasar.kvasir.page.ability.timer;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TimerAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "timer";

    String SUBNAME_STATUS = "status";

    String SUBNAME_SCHEDULEDDATE = "scheduledDate";

    String SUBNAME_COMPONENT = "component";

    String SUBNAME_BEGINDATE = "beginDate";

    String SUBNAME_FINISHDATE = "finishDate";

    String SUBNAME_ERRORINFORMATION = "errorInformation";


    Schedule getSchedule(Page page, int id);


    Schedule[] getSchedules(Page page);


    Schedule[] getSchedules(Page page, ScheduleStatus status);


    void updateSchedule(Page page, int id, ScheduleMold mold);


    void addSchedule(Page page, ScheduleMold mold);


    void removeSchedule(Page page, int id);


    void removeSchedules(Page page, ScheduleStatus status);


    void removeSchedules(ScheduleStatus status);


    void clearSchedules(Page page);


    void clearSchedules();
}
