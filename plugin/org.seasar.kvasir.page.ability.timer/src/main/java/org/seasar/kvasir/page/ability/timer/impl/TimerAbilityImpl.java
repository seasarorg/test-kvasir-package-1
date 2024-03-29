package org.seasar.kvasir.page.ability.timer.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.ScheduleMold;
import org.seasar.kvasir.page.ability.timer.TimerAbility;
import org.seasar.kvasir.page.ability.timer.TimerAbilityAlfr;


public class TimerAbilityImpl extends AbstractPageAbility
    implements TimerAbility
{
    private TimerAbilityAlfr alfr_;

    private Page page_;


    public TimerAbilityImpl(TimerAbilityAlfr alfr, Page page)
    {
        super(alfr, page);
        alfr_ = alfr;
        page_ = page;
    }


    public void addSchedule(ScheduleMold mold)
    {
        alfr_.addSchedule(page_, mold);
    }


    public void enableSchedule(int id, boolean enabled)
    {
        alfr_.enableSchedule(page_, id, enabled);
    }


    public void clearSchedules()
    {
        alfr_.clearSchedules(page_);
    }


    public Schedule getSchedule(int id)
    {
        return alfr_.getSchedule(page_, id);
    }


    public Schedule[] getSchedules()
    {
        return alfr_.getSchedules(page_);
    }


    public void removeSchedule(int id)
    {
        alfr_.removeSchedule(page_, id);
    }
}
