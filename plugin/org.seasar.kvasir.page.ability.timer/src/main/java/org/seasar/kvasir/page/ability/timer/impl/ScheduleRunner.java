package org.seasar.kvasir.page.ability.timer.impl;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.ScheduledComponent;


public class ScheduleRunner
    implements Runnable
{
    private Schedule schedule_;


    public ScheduleRunner(Schedule schedule)
    {
        schedule_ = schedule;
    }


    public void run()
    {
        Plugin<?> plugin = Asgard.getKvasir().getPluginAlfr().getPlugin(
            schedule_.getPluginId());
        Page page = Asgard.getKvasir().getPluginAlfr().getPlugin(
            PagePlugin.class).getPageAlfr().getPage(schedule_.getPageId());

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(
                plugin.getInnerClassLoader());

            ScheduledComponent component = (ScheduledComponent)plugin
                .getComponentContainer().getComponent(schedule_.getComponent());
            component.execute(page, schedule_.getParameter());
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
}
