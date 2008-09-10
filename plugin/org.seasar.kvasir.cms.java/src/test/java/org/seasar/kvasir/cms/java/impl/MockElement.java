package org.seasar.kvasir.cms.java.impl;

import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.cms.extension.PageProcessorElement;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MockElement extends PageProcessorElement
{
    @Override
    public Extension getParent()
    {
        return new MockExtension();
    }


    public class MockExtension extends Extension
    {
        @Override
        public PluginDescriptor getParent()
        {
            return new MockPluginDescriptor();
        }
    }

    public class MockPluginDescriptor extends PluginDescriptorImpl
    {
        public Plugin<?> getPlugin()
        {
            return new MockPlugin<Object>();
        }
    }
}
