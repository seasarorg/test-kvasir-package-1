package org.seasar.kvasir.cms.zpt.impl;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.EmptySettings;


public class ZptPluginImpl extends AbstractPlugin<EmptySettings>
{
    private ZptPageProcessor zptPageProcessor_;


    @Override
    protected boolean doStart()
    {
        zptPageProcessor_.start();

        return true;
    }


    @Override
    protected void doStop()
    {
        zptPageProcessor_ = null;
    }


    @Binding("zptPageProcessor")
    public void setZptPageProcessor(ZptPageProcessor zptPageProcessor)
    {
        zptPageProcessor_ = zptPageProcessor;
    }
}
