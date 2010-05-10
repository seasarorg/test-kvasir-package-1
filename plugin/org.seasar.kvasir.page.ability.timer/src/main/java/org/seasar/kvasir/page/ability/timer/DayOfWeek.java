package org.seasar.kvasir.page.ability.timer;

import java.util.Calendar;


public enum DayOfWeek
{
    SUNDAY(0, Calendar.SUNDAY), MONDAY(1, Calendar.MONDAY), TUESDAY(2,
        Calendar.TUESDAY), WEDNESDAY(3, Calendar.WEDNESDAY), THURSDAY(4,
        Calendar.THURSDAY), FRIDAY(5, Calendar.FRIDAY), SATURDAY(6,
        Calendar.SATURDAY);

    public static DayOfWeek enumOf(int id)
    {
        for (DayOfWeek enm : values()) {
            if (enm.getId() == id) {
                return enm;
            }
        }
        return null;
    }


    public static DayOfWeek enumOfCalendarDayOfWeek(int calendarDayOfWeek)
    {
        for (DayOfWeek enm : values()) {
            if (enm.getCalendarDayOfWeek() == calendarDayOfWeek) {
                return enm;
            }
        }
        return null;
    }


    private int id_;

    private int calendarDayOfWeek_;


    private DayOfWeek(int id, int calendarDayOfWeek)
    {
        id_ = id;
        calendarDayOfWeek_ = calendarDayOfWeek;
    }


    public int getId()
    {
        return id_;
    }


    public int getCalendarDayOfWeek()
    {
        return calendarDayOfWeek_;
    }
}
