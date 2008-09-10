package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;

public class Plugin5 extends AbstractPlugin<EmptySettings>
{
    @Override
    protected boolean doStart()
    {
        return false;
    }

    @Override
    protected void doStop()
    {
    }
}
