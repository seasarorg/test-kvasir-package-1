package org.seasar.kvasir.page.ability.timer;

import java.util.Date;


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


    ScheduleStatus getStatus();


    Date getScheduledDate();


    boolean isSucceed();


    String getComponent();


    Date getBeginDate();


    Date getFinishDate();


    String getErrorInformation();
}
