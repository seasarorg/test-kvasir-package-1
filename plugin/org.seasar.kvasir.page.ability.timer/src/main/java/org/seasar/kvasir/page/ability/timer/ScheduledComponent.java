package org.seasar.kvasir.page.ability.timer;

import org.seasar.kvasir.page.Page;


public interface ScheduledComponent
{
    void execute(Page page, String parameter);
}
