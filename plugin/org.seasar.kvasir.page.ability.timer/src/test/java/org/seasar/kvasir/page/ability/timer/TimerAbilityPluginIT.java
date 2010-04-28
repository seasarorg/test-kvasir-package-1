package org.seasar.kvasir.page.ability.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .parse("2010-04-01 12:00:00");
        ScheduleMold mold = new ScheduleMold(date, "component");
        ability.addSchedule(mold);

        // READ

        Schedule[] schedules = ability.getSchedules();
        assertEquals(1, schedules.length);

        assertEquals(ScheduleStatus.SCHEDULED, schedules[0].getStatus());
        assertEquals(date, schedules[0].getScheduledDate());
        assertEquals("component", schedules[0].getComponent());

        // CANCEL

        boolean cancelled = ability.cancelSchedule(schedules[0].getId());
        assertTrue(cancelled);

        Schedule schedule = ability.getSchedule(schedules[0].getId());
        assertEquals(ScheduleStatus.CANCELLED, schedule.getStatus());

        // DELETE

        ability.removeSchedule(schedules[0].getId());

        schedules = ability.getSchedules();
        assertEquals(0, schedules.length);
    }
}
