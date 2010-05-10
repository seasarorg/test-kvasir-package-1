package org.seasar.kvasir.page.ability.timer.dao;

import org.seasar.dao.annotation.tiger.Sql;


public interface ScheduleDao
{
    void insert(ScheduleDto dto);


    @Sql("SELECT * FROM schedule WHERE pageid=? and id=?")
    ScheduleDto selectByPageIdAndId(int pageId, int id);


    @Sql("SELECT * FROM schedule WHERE pageid=?")
    ScheduleDto[] selectListByPageId(int pageId);


    @Sql("SELECT * FROM schedule WHERE enabled=1")
    ScheduleDto[] selectEnabledList();


    @Sql("UPDATE schedule SET enabled=? WHERE pageid=? AND id=?")
    void updateEnabledByPageIdAndId(int enabled, int pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=? AND id=?")
    void deleteByPageIdAndId(int pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=?")
    void deleteByPageId(int pageId);


    @Sql("DELETE FROM schedule")
    void delete();
}
