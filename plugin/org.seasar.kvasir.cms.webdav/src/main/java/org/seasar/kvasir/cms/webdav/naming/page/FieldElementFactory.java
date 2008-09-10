package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedHashMap;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.NullResourceInfo;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;


public class FieldElementFactory extends AbstractPageElementFactory
{
    private static final String ATTRIBUTE = "field";

    private static final String SUFFIX_FIELD = "$" + ATTRIBUTE + ".xproperties";


    @Override
    public ResourceEntry analyzeResourceName(String name)
    {
        if (name == null) {
            return null;
        }
        if (!name.endsWith(SUFFIX_FIELD)) {
            return null;
        }

        return new ResourceEntry(this, name.substring(0, name.length()
            - SUFFIX_FIELD.length()), Page.VARIANT_DEFAULT, "");
    }


    protected PageElement constructElement0(String path, String pathname,
        Page page, ResourceEntry resource)
    {
        PageElement element = newElement(path, pathname, page, resource);

        element.setFile();
        if (page == null) {
            if (parentExists(pathname)) {
                element.setCanCreate();
            }
        } else {
            element.setCanDelete().setCanRead().setCanModify().setExists();
        }
        return element;
    }


    @Override
    protected boolean resourceExists(PageElement element)
    {
        return true;
    }


    @Override
    public ResourceInfo getResourceInfo(PageElement element)
    {
        Page page = element.getElement();
        if (page == null) {
            return NullResourceInfo.INSTANCE;
        }
        return new FieldResourceInfo(page);
    }


    @Override
    public String getAttribute()
    {
        return ATTRIBUTE;
    }


    protected void create(PageElement element, String encoding, InputStream in)
        throws NamingException
    {
        setContent(element, encoding, in);
    }


    protected void delete(PageElement element)
        throws NamingException
    {
        // 何もしない。
    }


    public String[] getResourceNames(Page page)
    {
        return new String[] { page.getName() + SUFFIX_FIELD };
    }


    protected void move(PageElement element, PageElement destination)
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }


    protected void setContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        Page page = getPage(element, true, false);
        PropertyHandler handler = new MapProperties(
            new LinkedHashMap<String, String>());
        try {
            handler.load(new InputStreamReader(in, encoding));
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        String asFile = handler.getProperty(Page.FIELD_ASFILE);
        if (asFile != null) {
            page.setAsFile(PropertyUtils.valueOf(asFile, false));
        }
        String concealDate = handler.getProperty(Page.FIELD_CONCEALDATE);
        if (concealDate != null) {
            try {
                page.setConcealDate(PageUtils.parseDate(concealDate));
            } catch (ParseException ex) {
                throw new IORuntimeException(
                    "Date format of concealDate is invalid: " + concealDate
                        + ": path=" + element.getPath(), ex);
            }
        }
        String createDate = handler.getProperty(Page.FIELD_CREATEDATE);
        if (createDate != null) {
            try {
                page.setCreateDate(PageUtils.parseDate(createDate));
            } catch (ParseException ex) {
                throw new IORuntimeException(
                    "Date format of createDate is invalid: " + createDate
                        + ": path=" + element.getPath(), ex);
            }
        }
        String listing = handler.getProperty(Page.FIELD_LISTING);
        if (listing != null) {
            page.setListing(PropertyUtils.valueOf(listing, false));
        }
        String lord = handler.getProperty(Page.FIELD_LORD);
        if (lord != null) {
            page.setAsLord(PropertyUtils.valueOf(lord, false));
        }
        String modifyDate = handler.getProperty(Page.FIELD_MODIFYDATE);
        if (modifyDate != null) {
            try {
                page.setModifyDate(PageUtils.parseDate(modifyDate));
            } catch (ParseException ex) {
                throw new IORuntimeException(
                    "Date format of modifyDate is invalid: " + modifyDate
                        + ": path=" + element.getPath(), ex);
            }
        }
        String node = handler.getProperty(Page.FIELD_NODE);
        if (node != null) {
            page.setNode(PropertyUtils.valueOf(node, false));
        }
        String orderNumber = handler.getProperty(Page.FIELD_ORDERNUMBER);
        if (orderNumber != null) {
            page.setOrderNumber(PropertyUtils.valueOf(orderNumber, 1));
        }
        String revealDate = handler.getProperty(Page.FIELD_REVEALDATE);
        if (revealDate != null) {
            try {
                page.setRevealDate(PageUtils.parseDate(revealDate));
            } catch (ParseException ex) {
                throw new IORuntimeException(
                    "Date format of revealDate is invalid: " + revealDate
                        + ": path=" + element.getPath(), ex);
            }
        }
    }
}
