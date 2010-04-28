package org.seasar.kvasir.page.ability.timer.dao;

import static org.seasar.kvasir.page.ability.timer.ScheduleStatusConstants.ID_CANCELLED;
import static org.seasar.kvasir.page.ability.timer.ScheduleStatusConstants.ID_INACTIVE;
import static org.seasar.kvasir.page.ability.timer.ScheduleStatusConstants.ID_SCHEDULED;

import org.seasar.dao.annotation.tiger.Sql;


public interface ScheduleDao
{
    void insert(ScheduleDto dto);


    @Sql("SELECT * FROM schedule WHERE pageid=? and id=?")
    ScheduleDto selectByPageIdAndId(int pageId, int id);


    @Sql("SELECT * FROM schedule WHERE pageid=?")
    ScheduleDto[] selectListByPageId(int pageId);


    @Sql("SELECT * FROM schedule WHERE pageid=? AND status=?")
    ScheduleDto[] selectListByPageIdAndStatus(int pageId, int status);


    @Sql("SELECT * FROM schedule WHERE status=? FOR UPDATE")
    ScheduleDto[] selectListForUpdateByStatus(int status);


    @Sql("UPDATE schedule SET status=? WHERE pageid=? AND id=?")
    void updateStatusByPageIdAndId(int status, int pageId, int id);


    @Sql("UPDATE schedule SET status=" + ID_CANCELLED
        + " WHERE pageid=? AND id=? AND status IN (" + ID_INACTIVE + ", "
        + ID_SCHEDULED + ")")
    int updateByPageIdAndIdToCancel(int pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=? AND id=?")
    void deleteByPageIdAndId(int pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=? AND status=?")
    void deleteByPageIdAndStatus(int pageId, int status);


    @Sql("DELETE FROM schedule WHERE pageid=?")
    void deleteByPageId(int pageId);


    @Sql("DELETE FROM schedule WHERE status=?")
    void deleteByStatus(int status);


    @Sql("DELETE FROM schedule")
    void delete();
}
