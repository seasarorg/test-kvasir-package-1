package org.seasar.kvasir.cms.java.impl;

import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MockPluginMetaData extends PluginDescriptorImpl
{
    public Plugin<?> getPlugin()
    {
        return new MockPlugin<Object>();
    }
}
