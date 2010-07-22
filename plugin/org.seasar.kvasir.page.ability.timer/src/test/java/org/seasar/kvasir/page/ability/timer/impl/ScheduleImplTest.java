package org.seasar.kvasir.page.ability.timer.impl;

import java.util.Calendar;

import junit.framework.TestCase;

import org.seasar.kvasir.page.ability.timer.dao.ScheduleDto;


public class ScheduleImplTest extends TestCase
{
    public void test_isMatched()
        throws Exception
    {
        ScheduleDto dto = new ScheduleDto();
        dto.setDayOfWeek("*");
        dto.setYear("2010");
        dto.setMonth("7");
        dto.setDay("21");
        dto.setHour("19");
        dto.setMinute("0");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);

        ScheduleImpl target = new ScheduleImpl(dto);
        assertTrue(target.isMatched(calendar));
    }
}
