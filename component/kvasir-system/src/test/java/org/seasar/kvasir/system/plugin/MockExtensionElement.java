package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.base.plugin.impl.PluginImpl;


/**
 * @author YOKOTA Takehiko
 */
public class MockExtensionElement extends AbstractElement
{
    private Plugin<?> plugin_ = new PluginImpl<Object>() {
        @Override
        public String getId()
        {
            return "dummy";
        }
    };


    public MockExtensionElement(String id, String action, String before,
        String after)
    {
        setId(id);
        if (action != null) {
            setAction(action);
        }
        if (before != null) {
            setBefore(before);
        }
        if (after != null) {
            setAfter(after);
        }
    }


    @Override
    public Plugin<?> getPlugin()
    {
        return plugin_;
    }


    @Override
    public Extension getParent()
    {
        return new Extension() {
            @Override
            public PluginDescriptor getParent()
            {
                return new PluginDescriptorImpl() {
                    @Override
                    public String getId()
                    {
                        return "dummy";
                    };


                    @Override
                    public Plugin<?> getPlugin()
                    {
                        return plugin_;
                    }
                };
            }
        };
    }
}
