package org.seasar.kvasir.cms.ymir.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import org.seasar.cms.pluggable.Configuration;
import org.seasar.cms.pluggable.impl.ConfigurationImpl;
import org.seasar.cms.ymir.impl.AbstractApplication;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.ymir.YmirApplication;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;


public class YmirApplicationImpl extends AbstractApplication
    implements YmirApplication
{
    private Plugin<?> plugin_;

    private String gardShortId_;

    private Configuration configuration_;

    private Resource webappRoot_;

    private Class<?> referenceClass_;

    private boolean underDevelopment_;

    private String propertiesPath_;


    public YmirApplicationImpl(String id, Plugin<?> plugin, String gardId,
        Resource webappRoot, String landmark, boolean underDevelopment)
    {
        super(id, null, null);
        plugin_ = plugin;
        gardShortId_ = getShortId(gardId);
        webappRoot_ = webappRoot;
        underDevelopment_ = underDevelopment;
        propertiesPath_ = "app." + gardShortId_ + ".properties";

        ConfigurationImpl configuration = new ConfigurationImpl();
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(
                plugin.getInnerClassLoader());

            configuration.load(propertiesPath_);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
        configuration_ = configuration;

        try {
            referenceClass_ = Class.forName(landmark, false, plugin_
                .getInnerClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new IORuntimeException(
                "Landmark class does not exist on the classloader: className="
                    + landmark + ", pluginId=" + plugin);
        }
    }


    String getShortId(String gardId)
    {
        if (gardId == null) {
            return null;
        }
        int dot = gardId.lastIndexOf('.');
        if (dot < 0) {
            return gardId;
        } else {
            return gardId.substring(dot + 1);
        }
    }


    public String getProperty(String key)
    {
        return configuration_.getProperty(key);
    }


    public String getProperty(String key, String defaultValue)
    {
        return configuration_.getProperty(key, defaultValue);
    }


    @SuppressWarnings("unchecked")
    public Enumeration propertyNames()
    {
        return configuration_.propertyNames();
    }


    public S2Container getS2Container()
    {
        return (S2Container)plugin_.getComponentContainer().getRawContainer();
    }


    @Override
    public String getWebappSourceRoot()
    {
        String webappSourceRoot = getProperty(KEY_WEBAPPSOURCEROOT);
        if (webappSourceRoot == null) {
            String projectRoot = getProjectRoot();
            if (projectRoot != null) {
                webappSourceRoot = projectRoot + "/src/main/plugin/gards/"
                    + gardShortId_ + "/static";
            } else {
                webappSourceRoot = getWebappRoot();
            }
        } else {
            String projectRoot = getProjectRoot();
            if (projectRoot != null) {
                webappSourceRoot = projectRoot + "/" + webappSourceRoot;
            }
        }
        return webappSourceRoot;
    }


    @SuppressWarnings("unchecked")
    public Class getReferenceClass()
    {
        return referenceClass_;
    }


    @SuppressWarnings("unchecked")
    public boolean isCapable(Class clazz)
    {
        return (plugin_.getInnerClassLoader().getResource(
            clazz.getName().replace('.', '/').concat(".class")) != null);
    }


    public void removeProperty(String key)
    {
        configuration_.removeProperty(key);
    }


    @Override
    public String getDefaultPropertiesFilePath()
    {
        String resourcesDirectory = getResourcesDirectory();
        if (resourcesDirectory == null) {
            return null;
        } else {
            return resourcesDirectory + "/" + propertiesPath_;
        }
    }


    public void save(OutputStream out, String header)
        throws IOException
    {
        configuration_.save(out, header);
    }


    public void setProperty(String key, String value)
    {
        configuration_.setProperty(key, value);
    }


    public boolean isUnderDevelopment()
    {
        return underDevelopment_;
    }


    public String getWebappRoot()
    {
        File file = webappRoot_.toFile();
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }


    public boolean isResourceExists(String path)
    {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Resource resource;
        if (path.length() == 0) {
            resource = webappRoot_;
        } else {
            resource = webappRoot_.getChildResource(path);
        }
        return resource.exists();
    }


    public String getClassesDirectory()
    {
        if (referenceClass_ != null) {
            File file = ResourceUtil
                .getResourceAsFileNoException(referenceClass_);
            if (file != null && file.exists()) {
                File baseDirectory = ClassUtils
                    .getBaseDirectory(referenceClass_);
                if (baseDirectory != null && baseDirectory.exists()) {
                    return baseDirectory.getAbsolutePath();
                }
            }
        }
        String projectRoot = getProjectRoot();
        if (projectRoot != null) {
            return projectRoot + "/build/classes";
        }
        return null;
    }


    public Resource getWebappRootResource()
    {
        return webappRoot_;
    }
}
