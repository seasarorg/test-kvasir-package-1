package org.seasar.kvasir.cms.webdav.naming.page;

import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_LABEL;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.naming.NamingException;

import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.cms.webdav.WebdavPlugin;
import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.InvalidPathException;
import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.AbstractElementFactory;
import org.seasar.kvasir.cms.webdav.naming.impl.ElementDirContext;
import org.seasar.kvasir.cms.webdav.setting.WebdavPluginSettings;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.type.DirectoryMold;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


abstract public class AbstractPageElementFactory extends
    AbstractElementFactory<Page>
    implements PageElementFactory
{
    protected PageAlfr pageAlfr_;

    protected WebdavPlugin plugin_;

    protected MimePlugin mimePlugin_;

    protected PageElementDirContext pageElementDirContext_;


    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public void setPlugin(WebdavPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setMimePlugin(MimePlugin mimePlugin)
    {
        mimePlugin_ = mimePlugin;
    }


    @Override
    public void setDirContext(ElementDirContext<Page> dirContext)
    {
        super.setDirContext(dirContext);
        pageElementDirContext_ = (PageElementDirContext)dirContext;
    }


    public Element<Page> newInstance(String path)
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }


    public String[] getChildPaths(Element<Page> element)
        throws NamingException
    {
        throw new UnsupportedOperationException();
    }


    abstract public String[] getResourceNames(Page page);


    protected Page getParent(String pathname)
    {
        String parentPathname = getParentPath(pathname);
        if (parentPathname != null) {
            return pageAlfr_.getPage(getHeimId(), parentPathname);
        } else {
            return null;
        }
    }


    protected boolean parentExists(String pathname)
    {
        return (getParent(pathname) != null);
    }


    protected PageElement newElement(String path, String pathname, Page page,
        ResourceEntry resource)
    {
        PageElement element = new PageElement(resource.getFactory(),
            getHeimId(), path);
        element.setAttribute(getAttribute());
        element.setVariant(resource.getVariant());
        element.setType(resource.getType());
        element.setElement(page);
        element.setPathname(pathname);

        return element;
    }


    protected Page getPage(PageElement pageElement, boolean create,
        boolean directory)
        throws NamingException
    {
        Page page = pageElement.getElement();
        if (page == null && create) {
            Page parent = getElement(getParentPath(pageElement.getPath()))
                .getElement();
            String name = getName(pageElement.getPathname());
            if (directory) {
                Page child;
                try {
                    child = parent
                        .createChild((DirectoryMold)new DirectoryMold()
                            .setName(name).setRevealDate(
                                getRevealDateValue(pageElement.getPath())));
                } catch (DuplicatePageException ex) {
                    throw (NamingException)new NamingException(
                        "Can't create element: element already exists: path="
                            + pageElement.getPath()).initCause(ex);
                }
                child.getAbility(PropertyAbility.class).setProperty(PROP_LABEL,
                    name);
            } else {
                try {
                    page = parent.createChild(new PageMold().setName(name)
                        .setRevealDate(
                            getRevealDateValue(pageElement.getPath())));
                } catch (DuplicatePageException ex) {
                    throw (NamingException)new NamingException(
                        "Can't create element: element already exists: path="
                            + pageElement.getPath()).initCause(ex);
                }
                PropertyAbility property = page
                    .getAbility(PropertyAbility.class);
                property.setProperty(PROP_LABEL, name);
            }
        }
        return page;
    }


    protected void createPageContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        setPageContent(element, encoding, in);
    }


    protected String getMimeType(String name)
    {
        WebdavPluginSettings settings = plugin_.getSettings();
        String mimeType = settings.getMimeType(name);
        if (mimeType == null) {
            mimeType = mimePlugin_.getMimeMappings().getMimeType(name);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
        }
        return mimeType;
    }


    protected void setPageContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        Page page = getPage(element, true, in == null);
        if (in != null) {
            String variant = element.getVariant();
            ContentAbility ability = page.getAbility(ContentAbility.class);
            Content content = ability.getLatestContent(variant);

            ContentMold mold = new ContentMold();
            String mediaType;
            if (content == null) {
                mediaType = getMimeType(element.getName());
                mold.setMediaType(mediaType);
            } else {
                mediaType = content.getMediaType();
            }
            if (mediaType.startsWith("text/")) {
                String internalEncoding = getInternalEncoding(content, element
                    .getPath());
                if (internalEncoding != null
                    && !internalEncoding.equals(encoding)) {
                    try {
                        in = new ByteArrayInputStream(IOUtils.readString(in,
                            encoding, false).getBytes(internalEncoding));
                    } catch (UnsupportedEncodingException ex) {
                        throw new IORuntimeException(ex);
                    }
                    encoding = internalEncoding;
                }
                mold.setEncoding(encoding);
            }
            mold.setBodyInputStream(in);
            if (content == null
                && !plugin_.getSettings().isEmbeddedContent(element.getName())) {
                page.setAsFile(true);
            }
            ability.setContent(variant, mold);
            page.touch();
        }
    }


    protected Content getLatestContent(Page page, String variant)
    {
        return page.getAbility(ContentAbility.class).getLatestContent(variant);
    }


    protected String getInternalEncoding(Content content, String path)
    {
        if (content != null) {
            return content.getEncoding();
        } else {
            return plugin_.getSettings().findInternalCharset(path);
        }
    }


    Date getRevealDateValue(String path)
    {
        if (plugin_.getSettings().shouldKeepConcealedWhenCreated(path)) {
            return Page.DATE_RAGNAROK;
        } else {
            return new Date();
        }
    }


    public final PageElement constructElement(String path, String pathname,
        Page page, ResourceEntry resource)
    {
        PageElement element = constructElement0(path, pathname, page, resource);
        if (element.exists()) {
            element.setResourceInfo(getResourceInfo(element));
        }
        return element;
    }


    protected PageElement constructElement0(String path, String pathname,
        Page page, ResourceEntry resource)
    {
        PageElement element = newElement(path, pathname, page, resource);

        element.setFile();
        if (page == null) {
            if (parentExists(pathname)) {
                // page == nullでも存在することにしているのは、削除時にPageオブジェクト自体が
                // 先に作成されてしまった後に削除されようとすることがあり、その際に「存在しない」
                // フラグが立っているElementを返すとWebDAVクライアント側でエラーになってしまうのを
                // 避けるため。
                element.setCanDelete().setCanModify().setCanRead().setExists();
            }
        } else {
            element.setCanDelete().setCanModify().setCanRead().setExists();
        }
        return element;
    }


    abstract protected boolean resourceExists(PageElement element);


    public final void delete(Element<Page> element)
        throws NamingException
    {
        delete((PageElement)element);
    }


    abstract protected void delete(PageElement element)
        throws NamingException;


    public final void setContent(Element<Page> element, String encoding,
        InputStream in)
        throws NamingException
    {
        setContent((PageElement)element, encoding, in);
    }


    abstract protected void setContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException;


    public final void move(Element<Page> element, Element<Page> destination)
        throws NamingException
    {
        PageElement pageElement = (PageElement)element;
        PageElement destinationElement = (PageElement)destination;
        if (pageElement.getAttribute()
            .equals(destinationElement.getAttribute())) {
            move(pageElement, destinationElement);
        } else {
            throw new InvalidPathException("Can't move: element="
                + element.getPath() + ", destination=" + destination.getPath());
        }
    }


    abstract protected void move(PageElement element, PageElement destination)
        throws NamingException;


    public final void create(Element<Page> element, String encoding,
        InputStream in)
        throws NamingException
    {
        create((PageElement)element, encoding, in);
    }


    abstract protected void create(PageElement element, String encoding,
        InputStream in)
        throws NamingException;


    abstract public String getAttribute();


    abstract public ResourceEntry analyzeResourceName(String name);


    public final ResourceInfo getResourceInfo(Element<Page> element)
    {
        return getResourceInfo((PageElement)element);
    }


    abstract protected ResourceInfo getResourceInfo(PageElement element);
}
