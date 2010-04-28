package org.seasar.kvasir.page.ability.timer;

import org.seasar.kvasir.page.ability.PageAbility;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TimerAbility
    extends PageAbility
{
    Schedule getSchedule(int id);


    Schedule[] getSchedules();


    Schedule[] getSchedules(ScheduleStatus status);


    boolean cancelSchedule(int id);


    void addSchedule(ScheduleMold mold);


    void removeSchedule(int id);


    void removeSchedules(ScheduleStatus status);


    void clearSchedules();
}
