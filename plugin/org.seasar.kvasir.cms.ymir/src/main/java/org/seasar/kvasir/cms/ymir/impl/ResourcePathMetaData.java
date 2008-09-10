package org.seasar.kvasir.cms.ymir.impl;

import java.io.File;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.extension.creator.PathMetaData;
import org.seasar.cms.ymir.extension.creator.Template;
import org.seasar.cms.ymir.extension.creator.impl.FileTemplate;
import org.seasar.kvasir.util.io.Resource;


public class ResourcePathMetaData
    implements PathMetaData
{
    private Resource resource_;


    public ResourcePathMetaData(Resource resource)
    {
        resource_ = resource;
    }


    public String getActionName()
    {
        return null;
    }


    public File getBaseSourceFile()
    {
        return null;
    }


    public String getClassName()
    {
        return null;
    }


    public String getComponentName()
    {
        return null;
    }


    public String getMethod()
    {
        return Request.METHOD_GET;
    }


    public String getPath()
    {
        return null;
    }


    public File getSourceFile()
    {
        return null;
    }


    public Template getTemplate()
    {
        File file = resource_.toFile();
        return new FileTemplate(file.getPath(), file);
    }


    public boolean isDenied()
    {
        return false;
    }
}
