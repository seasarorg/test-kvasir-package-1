package org.seasar.kvasir.cms.ymir.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import org.seasar.cms.pluggable.hotdeploy.LocalHotdeployS2Container;
import org.seasar.cms.pluggable.impl.PluggableNamingConventionImpl;
import org.seasar.cms.ymir.PathMappingProvider;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.cms.ymir.DelegatingApplication;
import org.seasar.kvasir.cms.ymir.YmirApplication;
import org.seasar.kvasir.util.io.Resource;


public class ApplicationImpl
    implements DelegatingApplication
{
    private YmirApplication application_;

    private LocalHotdeployS2Container hotdeployS2Container_;

    private PathMappingProvider pathMappingProvider_;


    @Binding("container.getComponent(@org.seasar.cms.pluggable.hotdeploy.LocalHotdeployS2Container@class)")
    public void setHotdeployS2Container(
        LocalHotdeployS2Container hotdeployContainer)
    {
        hotdeployS2Container_ = hotdeployContainer;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPathMappingProvider(PathMappingProvider pathMappingProvider)
    {
        pathMappingProvider_ = pathMappingProvider;
    }


    @Binding(bindingType = BindingType.NONE)
    public void setApplication(YmirApplication application)
    {
        application_ = application;

        ((PluggableNamingConventionImpl)hotdeployS2Container_
            .getNamingConvention()).addRootPackageName(getRootPackageName());

        if (isUnderDevelopment()) {
            hotdeployS2Container_.setClassesDirectory(application_
                .getClassesDirectory());
        } else {
            hotdeployS2Container_.setHotdeployDisabled();
        }

        String referenceClassName = application_.getReferenceClass().getName();
        boolean included = false;
        String[] referenceClassNames = hotdeployS2Container_
            .getReferenceClassNames();
        for (int i = 0; i < referenceClassNames.length; i++) {
            if (referenceClassName.equals(referenceClassNames[i])) {
                included = true;
                break;
            }
        }
        if (!included) {
            hotdeployS2Container_.addReferenceClassName(referenceClassName);
        }
    }


    /**
     * Application
     */

    public String getId()
    {
        return application_.getId();
    }


    public String getDefaultPropertiesFilePath()
    {
        return application_.getDefaultPropertiesFilePath();
    }


    public LocalHotdeployS2Container getHotdeployS2Container()
    {
        return hotdeployS2Container_;
    }


    public PathMappingProvider getPathMappingProvider()
    {
        return pathMappingProvider_;
    }


    public String getProjectRoot()
    {
        return application_.getProjectRoot();
    }


    public String getProperty(String key, String defaultValue)
    {
        return application_.getProperty(key, defaultValue);
    }


    public String getProperty(String key)
    {
        return application_.getProperty(key);
    }


    @SuppressWarnings("unchecked")
    public Enumeration propertyNames()
    {
        return application_.propertyNames();
    }


    @SuppressWarnings("unchecked")
    public Class getReferenceClass()
    {
        return application_.getReferenceClass();
    }


    public String getResourcesDirectory()
    {
        return application_.getResourcesDirectory();
    }


    public String getRootPackageName()
    {
        return application_.getRootPackageName();
    }


    public S2Container getS2Container()
    {
        return application_.getS2Container();
    }


    public String getSourceDirectory()
    {
        return application_.getSourceDirectory();
    }


    public String getWebappRoot()
    {
        return application_.getWebappRoot();
    }


    public String getWebappSourceRoot()
    {
        return application_.getWebappSourceRoot();
    }


    public boolean isUnderDevelopment()
    {
        return application_.isUnderDevelopment();
    }


    @SuppressWarnings("unchecked")
    public boolean isCapable(Class clazz)
    {
        return application_.isCapable(clazz);
    }


    public boolean isResourceExists(String path)
    {
        return application_.isResourceExists(path);
    }


    public void removeProperty(String key)
    {
        application_.removeProperty(key);
    }


    public void save(OutputStream out, String header)
        throws IOException
    {
        application_.save(out, header);
    }


    public void setProjectRoot(String projectRoot)
    {
        application_.setProjectRoot(projectRoot);
    }


    public void setProperty(String key, String value)
    {
        application_.setProperty(key, value);
    }


    public void setResourcesDirectory(String resourcesDirectory)
    {
        application_.setResourcesDirectory(resourcesDirectory);
    }


    public void setRootPackageName(String rootPackageName)
    {
        application_.setRootPackageName(rootPackageName);
    }


    public void setSourceDirectory(String sourceDirectory)
    {
        application_.setSourceDirectory(sourceDirectory);
    }


    public String getClassesDirectory()
    {
        return application_.getClassesDirectory();
    }


    public Resource getWebappRootResource()
    {
        return application_.getWebappRootResource();
    }
}
