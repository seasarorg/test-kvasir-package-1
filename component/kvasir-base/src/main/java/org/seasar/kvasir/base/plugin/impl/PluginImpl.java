package org.seasar.kvasir.base.plugin.impl;

import org.seasar.kvasir.base.plugin.AbstractPlugin;


/**
 * <p><b>同期化：</b>
 * このクラスは<code>start()</code>
 * が呼び出された後はスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class PluginImpl<S> extends AbstractPlugin<S>
{
    /*
     * constructors
     */

    public PluginImpl()
    {
    }


    /*
     * AbstractPlugin
     */

    @Override
    protected boolean doStart()
    {
        return true;
    }


    @Override
    protected void doStop()
    {
    }
}
