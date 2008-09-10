package org.seasar.kvasir.cms.webdav.naming.page;

import org.seasar.kvasir.cms.webdav.naming.ElementFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;


public interface PageElementFactory
    extends ElementFactory<Page>
{
    void setPageAlfr(PageAlfr pageAlfr);


    String getAttribute();


    ResourceEntry analyzeResourceName(String name);


    PageElement constructElement(String path, String pathname, Page page,
        ResourceEntry resource);


    String[] getResourceNames(Page page);


    public static class ResourceEntry
    {
        private PageElementFactory factory_;

        private String name_;

        private String variant_;

        private String type_;


        public ResourceEntry(PageElementFactory factory, String name,
            String variant, String type)
        {
            factory_ = factory;
            name_ = name;
            variant_ = variant;
            type_ = type;
        }


        public PageElementFactory getFactory()
        {
            return factory_;
        }


        public String getName()
        {
            return name_;
        }


        public String getType()
        {
            return type_;
        }


        public String getVariant()
        {
            return variant_;
        }
    }
}
