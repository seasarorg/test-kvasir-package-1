package org.seasar.kvasir.cms.webdav.naming.page;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;
import org.seasar.kvasir.cms.webdav.naming.impl.ElementImpl;
import org.seasar.kvasir.page.Page;


public class PageElement extends ElementImpl<Page>
{
    private String pathname_;

    private String variant_ = Page.VARIANT_DEFAULT;

    private String type_ = "";

    protected final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public PageElement(ElementFactory<Page> elementFactory, int heimId,
        String path)
    {
        super(elementFactory, heimId, path);
    }


    public String getPathname()
    {
        return pathname_;
    }


    public void setPathname(String pathname)
    {
        pathname_ = pathname;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }
}
