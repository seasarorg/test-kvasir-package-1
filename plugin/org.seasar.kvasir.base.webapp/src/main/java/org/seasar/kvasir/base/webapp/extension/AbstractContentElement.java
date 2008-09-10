package org.seasar.kvasir.base.webapp.extension;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.mime.MimeMappings;
import org.seasar.kvasir.base.webapp.Content;
import org.seasar.kvasir.base.webapp.impl.ResourceContent;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;


abstract public class AbstractContentElement extends AbstractElement
{
    private String resourcePath_;

    private Resource resource_;


    public Resource getResource()
    {
        if (resource_ == null) {
            resource_ = getBaseResource().getChildResource(resourcePath_);
        }
        return resource_;
    }


    public String getResourcePath()
    {
        return resourcePath_;
    }


    @Attribute
    @Required
    public void setResourcePath(String resourcePath)
    {
        resourcePath_ = resourcePath;
    }


    Resource getBaseResource()
    {
        return getPlugin().getHomeDirectory();
    }


    public String[] getExpandedPaths()
    {
        List<String> pathList = new ArrayList<String>();
        getExpandedPaths(getResource(), getPath(), pathList);
        return pathList.toArray(new String[0]);
    }


    void getExpandedPaths(Resource resource, String path, List<String> pathList)
    {
        if (!resource.exists()) {
            return;
        }
        if (resource.isDirectory()) {
            Resource[] children = resource.listResources();
            for (int i = 0; i < children.length; i++) {
                getExpandedPaths(children[i], path + "/"
                    + children[i].getName(), pathList);
            }
        } else {
            pathList.add(path);
        }
    }


    abstract protected String getPath();


    public Content getContent(String path, MimeMappings mappings)
    {
        if (!path.startsWith(getPath())) {
            return null;
        }
        return new ResourceContent(getResource().getChildResource(
            path.substring(getPath().length())), getContentType(path, mappings));
    }


    protected String getContentType(String path, MimeMappings mappings)
    {
        String contentType = null;
        if (mappings != null) {
            contentType = mappings.getMimeType(path);
        }
        if (contentType == null) {
            contentType = getDefaultContentType();
        }
        return contentType;
    }


    abstract protected String getDefaultContentType();
}
