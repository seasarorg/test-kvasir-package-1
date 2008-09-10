package org.seasar.kvasir.base.mock.plugin;

import java.util.Locale;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.impl.ConsoleKvasirLog;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.SettingsListener;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.util.io.Resource;


public class MockPlugin<S>
    implements Plugin<S>
{
    private String id_;

    private Version version_;

    private PluginDescriptor descriptor_;

    private ComponentContainer container_;

    private Resource projectDirectory_;

    private boolean underDevelopment_;

    private PluginProperties prop_;

    private Resource homeSourceDirectory_;


    public MockPlugin()
    {
    }


    public MockPlugin(String id, Resource projectDirectory,
        PluginDescriptor descriptor)
    {
        descriptor.setPlugin(this);

        setId(id);
        setVersion(new Version("1.0.0"));
        setProjectDirectory(projectDirectory);
        setDescriptor(descriptor);
        setProperties(descriptor.loadPluginProperties());
    }


    public void addToLifecycle(Object object)
        throws IllegalStateException
    {
    }


    public ComponentContainer getComponentContainer()
    {
        return container_;
    }


    public Resource getConfigurationDirectory()
    {
        return descriptor_.getConfigurationDirectory();
    }


    public PluginDescriptor getDescriptor()
    {
        return descriptor_;
    }


    public Object[] getExtensionComponents(String point)
    {
        return null;
    }


    public Object[] getExtensionComponents(String point, boolean ascending)
    {
        return null;
    }


    public <T> T[] getExtensionComponents(Class<T> componentClass)
    {
        return null;
    }


    public <T> T[] getExtensionComponents(Class<T> componentClass,
        boolean ascending)
    {
        return null;
    }


    public ExtensionElement[] getExtensionElements(String point)
    {
        return null;
    }


    public ExtensionElement[] getExtensionElements(String point,
        boolean ascending)
    {
        return null;
    }


    public <T> T[] getExtensionElements(Class<T> elementClass)
    {
        return null;
    }


    public <T> T[] getExtensionElements(Class<T> elementClass, boolean ascending)
    {
        return null;
    }


    public Resource getHomeDirectory()
    {
        return homeSourceDirectory_;
    }


    public String getId()
    {
        return id_;
    }


    public void setId(String id)
    {
        id_ = id;
    }


    public ClassLoader getInnerClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }


    public KvasirLog getLog()
    {
        return new ConsoleKvasirLog();
    }


    public ClassLoader getOuterClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }


    public PluginProperties getProperties()
    {
        return prop_;
    }


    public String getProperty(String name)
    {
        return prop_.getProperty(name);
    }


    public String getProperty(String name, String variant)
    {
        return prop_.getProperty(name, variant);
    }


    public String getProperty(String name, Locale locale)
    {
        return prop_.getProperty(name, locale);
    }


    public S getSettings()
    {
        return null;
    }


    public Class<S> getSettingsClass()
    {
        return null;
    }


    public S getSettingsForUpdate()
    {
        return null;
    }


    public <T> T getStructuredProperty(String name, Class<T> structureClass)
    {
        return null;
    }


    public Version getVersion()
    {
        return version_;
    }


    public void setVersion(Version version)
    {
        version_ = version;
    }


    public boolean isDisabled()
    {
        return false;
    }


    public boolean isStarted()
    {
        return false;
    }


    public boolean isUnderDevelopment()
    {
        return underDevelopment_;
    }


    public S newSettings()
    {
        return null;
    }


    public void notifyKvasirStarted()
    {
    }


    public void reloadProperties()
    {
    }


    public String resolveString(String str)
    {
        return resolveString(str, Locale.getDefault());
    }


    public String resolveString(String str, Locale locale)
    {
        return resolveString(str, locale, false);
    }


    public String resolveString(String str, Locale locale,
        boolean returnNullIfNotExists)
    {
        if (str == null) {
            return null;
        } else if (str.startsWith("%")) {
            String key = str.substring(1);
            String resolved = prop_.getProperty(key, locale);
            if (resolved != null) {
                return resolved;
            } else {
                if (returnNullIfNotExists) {
                    return null;
                } else {
                    return "!" + key + "!";
                }
            }
        } else {
            return str;
        }
    }


    public void setComponentContainer(ComponentContainer container)
    {
        container_ = container;
    }


    public void setDescriptor(PluginDescriptor descriptor)
    {
        descriptor_ = descriptor;
    }


    public void setInnerClassLoader(ClassLoader innerClassLoader)
    {
    }


    public void setKvasir(Kvasir kvasir)
    {
    }


    public void setOuterClassLoader(ClassLoader outerClassLoader)
    {
    }


    public void setPluginAlfr(PluginAlfr pluginAlfr)
    {
    }


    public void setProperties(PluginProperties prop)
    {
        prop_ = prop;
    }


    public void setStructuredProperty(String name, Object structure)
    {
    }


    public void setUnderDevelopment(boolean underDevelopment)
    {
        underDevelopment_ = underDevelopment;
    }


    public Resource getProjectDirectory()
    {
        return projectDirectory_;
    }


    public Resource getHomeSourceDirectory()
    {
        return homeSourceDirectory_;
    }


    public void setProjectDirectory(Resource projectDirectory)
    {
        projectDirectory_ = projectDirectory;
        if (projectDirectory_ != null) {
            homeSourceDirectory_ = projectDirectory
                .getChildResource("src/main/plugin");
        }
    }


    public void storeProperties()
    {
    }


    public void storeSettings(S settings)
    {
    }


    public void resetSettings()
    {
    }


    public Object getAdapter(Object key)
    {
        return null;
    }


    public <T> T getAdapter(Class<T> key)
    {
        return null;
    }


    public boolean start()
    {
        return false;
    }


    public void stop()
    {
    }


    public void refresh()
    {
    }


    public void addSettingsListener(SettingsListener<S> listener)
    {
    }


    public void notifySettingsUpdated(SettingsEvent<S> eveent)
    {
    }
}
