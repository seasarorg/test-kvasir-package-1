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

    String SUBNAME_DAYOFWEEK = "week";

    String SUBNAME_YEAR = "year";

    String SUBNAME_MONTH = "month";

    String SUBNAME_DAY = "day";

    String SUBNAME_HOUR = "hour";

    String SUBNAME_MINUTE = "minute";

    String SUBNAME_PLUGINID = "pluginId";

    String SUBNAME_COMPONENT = "component";

    String SUBNAME_PARAMETER = "parameter";

    String SUBNAME_ENABLED = "enabled";


    Schedule getSchedule(Page page, int id);


    Schedule[] getSchedules(Page page);


    Schedule[] getEnabledSchedules();


    void enableSchedule(Page page, int id, boolean enabled);


    void addSchedule(Page page, ScheduleMold mold);


    void removeSchedule(Page page, int id);


    void clearSchedules(Page page);


    void clearSchedules();
}
