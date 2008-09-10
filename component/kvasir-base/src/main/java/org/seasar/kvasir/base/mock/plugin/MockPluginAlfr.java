package org.seasar.kvasir.base.mock.plugin;

import java.lang.reflect.Array;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.PluginUpdater;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MockPluginAlfr
    implements PluginAlfr
{
    private PluginAlfrSettings settings_;

    private PluginUpdater pluginUpdater_;

    private PluginDescriptor[] allPluginDescriptors_;


    public boolean start()
    {
        return true;
    }


    public void stop()
    {
    }


    public boolean isStarted()
    {
        return true;
    }


    public Plugin<?> getPlugin(final String id)
    {
        return null;
    }


    public Plugin<?>[] getPlugins()
    {
        return new Plugin[0];
    }


    public <T extends Plugin<?>> T getPlugin(final Class<T> clazz)
    {
        return null;
    }


    public boolean notifyKvasirStarted()
    {
        return true;
    }


    public ExtensionPoint getExtensionPoint(final String point)
    {
        return null;
    }


    public ExtensionPoint getExtensionPoint(final Object key,
        final String pluginId)
    {
        return null;
    }


    public Object[] getExtensionComponents(final String point,
        final boolean ascending)
    {
        return new Object[0];
    }


    @SuppressWarnings("unchecked")
    public <T> T[] getExtensionComponents(final Class<T> componentClass,
        final boolean ascending)
    {
        return (T[])Array.newInstance(componentClass, 0);
    }


    public ExtensionElement[] getExtensionElements(final String point,
        final boolean ascending)
    {
        return new ExtensionElement[0];
    }


    @SuppressWarnings("unchecked")
    public <T> T[] getExtensionElements(final Class<T> elementClass,
        final String pluginId, final boolean ascending)
    {
        return (T[])Array.newInstance(elementClass, 0);
    }


    public ExtensionPoint getExtensionPoint(final Object key)
    {
        return null;
    }


    public <T> T[] getExtensionComponents(final Class<T> componentClass,
        final String pluginId, final boolean ascending)
    {
        return null;
    }


    public Object[] getExtensionComponents(final String point)
    {
        return null;
    }


    public <T> T[] getExtensionComponents(final Class<T> componentClass,
        final String pluginId)
    {
        return null;
    }


    public ExtensionElement[] getExtensionElements(final String point)
    {
        return null;
    }


    public <T> T[] getExtensionElements(final Class<T> elementClass,
        final String pluginId)
    {
        return null;
    }


    public String[] getFailedPluginIdsToStart()
    {
        return new String[0];
    }


    public PluginUpdater getPluginUpdater()
    {
        return pluginUpdater_;
    }


    public PluginAlfrSettings getSettings()
    {
        return settings_;
    }


    public PluginAlfrSettings getSettingsForUpdate()
    {
        return null;
    }


    public void storeSettings(final PluginAlfrSettings settings)
    {
        settings_ = settings;
    }


    public void setPluginUpdater(final PluginUpdater pluginUpdater)
    {
        pluginUpdater_ = pluginUpdater;
    }


    public void setSettings(final PluginAlfrSettings settings)
    {
        settings_ = settings;
    }


    public PluginDescriptor[] getAllPluginDescriptors()
    {
        if (allPluginDescriptors_ == null) {
            allPluginDescriptors_ = new PluginDescriptor[0];
        }
        return allPluginDescriptors_;
    }


    public void setAllPluginDescriptors(PluginDescriptor[] allPluginDescriptors)
    {
        allPluginDescriptors_ = allPluginDescriptors;
    }


    public Plugin<?> getPluginUnderDevelopment()
    {
        return null;
    }
}
