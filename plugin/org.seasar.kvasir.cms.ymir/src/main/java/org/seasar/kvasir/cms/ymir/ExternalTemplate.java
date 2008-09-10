package org.seasar.kvasir.cms.ymir;

import org.seasar.kvasir.util.io.Resource;


public interface ExternalTemplate
{
    String getApplicationId();


    ResourceEntry[] getResourceEntries();


    String[] getIgnoreVariables();


    public static class ResourceEntry
    {
        private String path_;

        private Resource resource_;


        public ResourceEntry(Resource baseResource, Resource resource)
        {
            String baseURL = baseResource.getURL().toExternalForm();
            String resourceURL = resource.getURL().toExternalForm();
            if (!resourceURL.startsWith(baseURL)) {
                throw new IllegalArgumentException(
                    "Specified resource is not a child of base resource: resource="
                        + resourceURL + ", baseResource=" + baseURL);
            }
            path_ = resourceURL.substring(baseURL.length());
            if (path_.startsWith("/")) {
                path_ = path_.substring(1/*= "/".length() */);
            }
            resource_ = resource;
        }


        public String getPath()
        {
            return path_;
        }


        public Resource getResource()
        {
            return resource_;
        }
    }
}
