package org.seasar.kvasir.page.ability.timer.impl;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.timer.AbstractJob;
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.TimerAbilityAlfr;
import org.seasar.kvasir.page.ability.timer.TimerAbilityPlugin;


public class SchedulerJob extends AbstractJob
{
    @Binding(bindingType = BindingType.MUST)
    protected TimerAbilityPlugin timerAbilityPlugin_;

    @Binding(bindingType = BindingType.MUST)
    protected TimerAbilityAlfr timerAbilityAlfr_;

    private ExecutorService executorService_;


    @Override
    public void init()
    {
        int size = timerAbilityPlugin_.getSettings().getThreadPoolSize();
        if (size > 0) {
            executorService_ = Executors.newFixedThreadPool(size);
        } else {
            executorService_ = Executors.newCachedThreadPool();
        }
    }


    public void run()
    {
        Calendar calendar = Calendar.getInstance();

        for (Schedule schedule : timerAbilityAlfr_.getEnabledSchedules()) {
            if (schedule.isMatched(calendar)) {
                executorService_.execute(new ScheduleRunner(schedule));
            }
        }
    }


    @Override
    public void destroy()
    {
        executorService_.shutdown();
    }
}
