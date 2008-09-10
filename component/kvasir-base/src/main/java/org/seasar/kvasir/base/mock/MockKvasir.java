package org.seasar.kvasir.base.mock;

import java.util.Enumeration;

import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.KvasirProperties;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.util.io.Resource;


/**
 * @author YOKOTA Takehiko
 * @author manhole
 */
public class MockKvasir
    implements Kvasir
{

    private ClassLoader commonClassLoader_;

    private Resource configurationDirectory_;

    private Resource runtimeWorkDirectory_;

    private Resource homeDirectory_;

    private Resource systemDirectory_;

    private Resource pluginsDirectory_;

    private Resource stagingDirectory_;


    public boolean start(final KvasirProperties prop,
        final ComponentContainer container,
        final ClassLoader commonClassLoader, final ClassLoader systemClassLoader)
    {
        return true;
    }


    public void stop(final int timeoutSeconds)
    {
    }


    public boolean isStarted()
    {
        return false;
    }


    public boolean beginSession()
    {
        return false;
    }


    public long endSession()
    {
        return 0L;
    }


    public boolean isInSession()
    {
        return false;
    }


    public ClassLoader getCurrentClassLoader()
    {
        return null;
    }


    public Version getVersion()
    {
        return null;
    }


    public long getBuildNumber()
    {
        return 0L;
    }


    public Resource getHomeDirectory()
    {
        return homeDirectory_;
    }


    public void setHomeDirectory(final Resource homeDirectory)
    {
        homeDirectory_ = homeDirectory;
    }


    public Resource getConfigurationDirectory()
    {
        if (configurationDirectory_ == null && homeDirectory_ != null) {
            configurationDirectory_ = homeDirectory_
                .getChildResource(Globals.CONFIGURATION_DIR);
            configurationDirectory_.mkdirs();
        }
        return configurationDirectory_;
    }


    public void setConfigurationDirectory(final Resource configurationDirectory)
    {
        configurationDirectory_ = configurationDirectory;
    }


    public Resource getSystemDirectory()
    {
        if (systemDirectory_ == null && homeDirectory_ != null) {
            systemDirectory_ = homeDirectory_
                .getChildResource(Globals.SYSTEM_DIR);
            systemDirectory_.mkdirs();
        }
        return systemDirectory_;
    }


    public void setSystemDirectory(final Resource systemDirectory)
    {
        systemDirectory_ = systemDirectory;
    }


    public Resource getRuntimeWorkDirectory()
    {
        if (runtimeWorkDirectory_ == null && homeDirectory_ != null) {
            runtimeWorkDirectory_ = homeDirectory_
                .getChildResource(Globals.RUNTIMEWORK_DIR);
            runtimeWorkDirectory_.mkdirs();
        }
        return runtimeWorkDirectory_;
    }


    public void setRuntimeWorkDirectory(final Resource runtimeWorkDirectory)
    {
        runtimeWorkDirectory_ = runtimeWorkDirectory;
    }


    public Resource getPluginsDirectory()
    {
        if (pluginsDirectory_ == null && homeDirectory_ != null) {
            pluginsDirectory_ = homeDirectory_
                .getChildResource(Globals.PLUGINS_DIR);
            pluginsDirectory_.mkdirs();
        }
        return pluginsDirectory_;
    }


    public void setPluginsDirectory(final Resource pluginsDirectory)
    {
        pluginsDirectory_ = pluginsDirectory;
    }


    public Resource getStagingDirectory()
    {
        if (stagingDirectory_ == null && homeDirectory_ != null) {
            stagingDirectory_ = homeDirectory_
                .getChildResource(Globals.STAGING_DIR);
            stagingDirectory_.mkdirs();
        }
        return stagingDirectory_;
    }


    public void setStagingDirectory(Resource stagingDirectory)
    {
        stagingDirectory_ = stagingDirectory;
    }


    public ClassLoader getCommonClassLoader()
    {
        if (commonClassLoader_ != null) {
            return commonClassLoader_;
        } else {
            return getClass().getClassLoader();
        }
    }


    public void setCommonClassLoader(final ClassLoader commonClassLoader)
    {
        commonClassLoader_ = commonClassLoader;
    }


    public ClassLoader getClassLoader()
    {
        return null;
    }


    public String getProperty(final String name)
    {
        return null;
    }


    public String getProperty(final String name, final String defaultValue)
    {
        return null;
    }


    public int getProperty(final String name, final int defaultValue)
    {
        return 0;
    }


    public void setProperty(final String name, final String value)
    {
    }


    public void removeProperty(final String name)
    {
    }


    public Enumeration<String> propertyNames()
    {
        return null;
    }


    public void storeProperties()
    {
    }


    public ComponentContainer getComponentContainer()
    {
        return null;
    }


    public PluginAlfr getPluginAlfr()
    {
        return null;
    }


    public ExtensionPoint getExtensionPoint(final Object key)
    {
        return null;
    }


    public Object getAttribute(final String name)
    {
        return null;
    }


    public Enumeration<String> getAttributeNames()
    {
        return null;
    }


    public void removeAttribute(final String name)
    {
    }


    public void setAttribute(final String name, final Object value)
    {
    }


    public ComponentContainer getRootComponentContainer()
    {
        return null;
    }


    public boolean isUnderDevelopment()
    {
        return false;
    }


    public <T> T getStructuredProperty(final String name,
        final Class<T> structureClass)
    {
        return null;
    }


    public void setStructuredProperty(final String name, final Object metaData)
    {
    }


    public boolean isStandalone()
    {
        return true;
    }

}
