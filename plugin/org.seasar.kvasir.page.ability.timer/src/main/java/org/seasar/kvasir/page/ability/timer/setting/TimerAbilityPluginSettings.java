package org.seasar.kvasir.page.ability.timer.setting;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;


public class TimerAbilityPluginSettings
{
    private Integer threadPoolSize = 5;


    public Integer getThreadPoolSize()
    {
        return threadPoolSize;
    }


    @Attribute
    @Default("5")
    public void setThreadPoolSize(Integer threadPoolSize)
    {
        this.threadPoolSize = threadPoolSize;
    }
}
