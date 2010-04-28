package org.seasar.kvasir.page.ability.timer.dao;

import org.seasar.dao.annotation.tiger.Sql;


public interface ScheduleDao
{
    void insert(ScheduleDto dto);


    @Sql("SELECT * FROM schedule WHERE pageid=? and id=?")
    ScheduleDto selectByPageIdAndId(int pageId, int id);


    @Sql("SELECT * FROM schedule WHERE pageid=?")
    ScheduleDto[] selectListByPageId(Integer pageId);


    @Sql("SELECT * FROM schedule WHERE pageid=? AND status=?")
    ScheduleDto[] selectListByPageIdAndStatus(int pageId, int status);


    @Sql("pageid=? AND id=?")
    void updateByPageIdAndId(ScheduleDto dto, Integer pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=? AND id=?")
    void deleteByPageIdAndId(int pageId, int id);


    @Sql("DELETE FROM schedule WHERE pageid=? AND status=?")
    void deleteByPageIdAndStatus(int pageId, int status);


    @Sql("DELETE FROM schedule WHERE pageid=?")
    void deleteByPageId(Integer pageId);


    @Sql("DELETE FROM schedule WHERE status=?")
    void deleteByStatus(int status);


    @Sql("DELETE FROM schedule")
    void delete();
}
