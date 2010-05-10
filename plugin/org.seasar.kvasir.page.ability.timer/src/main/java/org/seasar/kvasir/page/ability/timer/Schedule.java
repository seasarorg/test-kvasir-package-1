package org.seasar.kvasir.page.ability.timer;

import java.util.Calendar;


/**
 * ページ毎に設定されたジョブの実行スケジュールを表わすインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Schedule
{
    int getId();


    int getPageId();


    CronFields getDayOfWeek();


    CronFields getYear();


    CronFields getMonth();


    CronFields getDay();


    CronFields getHour();


    CronFields getMinute();


    String getPluginId();


    String getComponent();


    String getParameter();


    boolean isEnabled();


    boolean isMatched(Calendar calendar);
}
