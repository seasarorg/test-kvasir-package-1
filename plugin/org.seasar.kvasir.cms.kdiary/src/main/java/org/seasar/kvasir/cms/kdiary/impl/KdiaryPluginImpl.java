package org.seasar.kvasir.cms.kdiary.impl;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;

import org.seasar.kvasir.cms.kdiary.KdiaryPlugin;


public class KdiaryPluginImpl extends AbstractPlugin<EmptySettings>
    implements KdiaryPlugin
{
    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
