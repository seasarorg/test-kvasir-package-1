package org.seasar.kvasir.page.ability.timer;

import junit.framework.Test;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class TimerAbilityPluginIT extends
    KvasirPluginTestCase<TimerAbilityPlugin>
{
    protected String getTargetPluginId()
    {
        return TimerAbilityPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(TimerAbilityPluginIT.class, false);
    }


    public void test_CRUD()
        throws Exception
    {
        PageAlfr pageAlfr = getComponent(PageAlfr.class);
        Page rootPage = pageAlfr.getRootPage(PathId.HEIM_MIDGARD);

        TimerAbility ability = rootPage.getAbility(TimerAbility.class);
        assertNotNull(ability);

        // CREATE

        ScheduleMold mold = new ScheduleMold(CronFields.every(), CronFields
            .of(1), CronFields.of(2), CronFields.of(3), CronFields.of(4),
            CronFields.of(5), "org.seasar.kvasir.page.ability.timer",
            "component");
        ability.addSchedule(mold);

        // READ

        Schedule[] schedules = ability.getSchedules();
        assertEquals(1, schedules.length);

        assertEquals("*", schedules[0].getDayOfWeek().toString());
        assertEquals("1", schedules[0].getYear().toString());
        assertEquals("2", schedules[0].getMonth().toString());
        assertEquals("3", schedules[0].getDay().toString());
        assertEquals("4", schedules[0].getHour().toString());
        assertEquals("5", schedules[0].getMinute().toString());
        assertEquals("org.seasar.kvasir.page.ability.timer", schedules[0]
            .getPluginId());
        assertEquals("component", schedules[0].getComponent());
        assertNull(schedules[0].getParameter());
        assertTrue(schedules[0].isEnabled());

        // UPDATE

        ability.enableSchedule(schedules[0].getId(), false);
        Schedule schedule = ability.getSchedule(schedules[0].getId());
        assertNotNull(schedule);
        assertFalse(schedule.isEnabled());

        // DELETE

        ability.removeSchedule(schedules[0].getId());

        schedules = ability.getSchedules();
        assertEquals(0, schedules.length);
    }
}
