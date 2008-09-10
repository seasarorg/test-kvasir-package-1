package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.InputStream;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.InvalidPathException;
import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.NullResourceInfo;
import org.seasar.kvasir.cms.webdav.naming.impl.ResourceInfoAdapter;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.util.io.IOUtils;


public class BasePageElementFactory extends AbstractPageElementFactory
{
    public static final String ATTRIBUTE = "page";

    public static final String TYPE_ATTACHED = "attached";

    public static final String TYPE_SUPERROOT = "superroot";


    @Override
    public ResourceEntry analyzeResourceName(String name)
    {
        if (name == null) {
            return new ResourceEntry(this, null, Page.VARIANT_DEFAULT,
                TYPE_SUPERROOT);
        }

        String pageName = pageElementDirContext_
            .getNameFromAttachedDirectoryName(name);
        if (pageName != null) {
            return new ResourceEntry(this, pageName, Page.VARIANT_DEFAULT,
                TYPE_ATTACHED);
        } else {
            return new ResourceEntry(this, name, Page.VARIANT_DEFAULT, "");
        }
    }


    protected PageElement constructElement0(String path, String pathname,
        Page page, ResourceEntry resource)
    {
        PageElement element = newElement(path, pathname, page, resource);

        if (TYPE_SUPERROOT.equals(resource.getType())) {
            element.setCanRead().setCollection().setExists();
        } else if (TYPE_ATTACHED.equals(resource.getType())) {
            if (page == null) {
                if (parentExists(pathname)) {
                    // page == nullでも存在することにしているのは、削除時にPageオブジェクト自体が
                    // 先に作成されてしまった後に削除されようとすることがあり、その際に「存在しない」
                    // フラグが立っているElementを返すとWebDAVクライアント側でエラーになってしまうのを
                    // 避けるため。
                    element.setCanDelete().setCanRead().setCanModify()
                        .setCollection().setExists();
                }
            } else if (!page.isNode()) {
                element.setCanDelete().setCanRead().setCanModify()
                    .setCollection().setExists();
            }
        } else {
            if (page == null) {
                if (parentExists(pathname)) {
                    element.setCanCreate().setCollection().setFile();
                }
            } else {
                element.setCanRead().setExists();
                if (!page.isRoot()) {
                    element.setCanDelete().setCanModify().setCanMove();
                }
                if (page.isNode()) {
                    element.setCollection();
                } else {
                    element.setFile();
                }
            }
        }
        return element;
    }


    @Override
    protected boolean resourceExists(PageElement element)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    protected void create(PageElement element, String encoding, InputStream in)
        throws NamingException
    {
        if (TYPE_ATTACHED.equals(element.getType())) {
            // inはnullのはずだが、念のため。
            IOUtils.closeQuietly(in);

            // ページがなければ作る。
            getPage(element, true, false);
        } else {
            createPageContent(element, encoding, in);
        }
    }


    @Override
    protected void delete(PageElement element)
        throws NamingException
    {
        if (TYPE_ATTACHED.equals(element.getType())) {
            Page page = element.getElement();
            if (page != null) {
                Page[] children = page.getChildren();
                for (int i = 0; i < children.length; i++) {
                    children[i].delete();
                }
            }
        } else {
            element.getElement().delete();
        }
    }


    @Override
    public String getAttribute()
    {
        return ATTRIBUTE;
    }


    @Override
    public String[] getResourceNames(Page page)
    {
        if (page.isNode()) {
            return new String[] { page.getName() };
        } else {
            return new String[] { page.getName(),
                pageElementDirContext_.getAttachedDirectoryName(page.getName()) };
        }
    }


    @Override
    protected ResourceInfo getResourceInfo(PageElement element)
    {
        Page page = element.getElement();
        if (page == null) {
            return NullResourceInfo.INSTANCE;
        }
        if (TYPE_SUPERROOT.equals(element.getType())) {
            return NullResourceInfo.INSTANCE;
        } else {
            if (page.isNode()) {
                return new PageResourceInfo(page);
            } else {
                Content content = getLatestContent(page, element.getVariant());
                if (content != null) {
                    return new ResourceInfoAdapter(content.getBodyResource());
                } else {
                    return new PageResourceInfo(page);
                }
            }
        }
    }


    @Override
    protected void move(PageElement element, PageElement destination)
        throws NamingException
    {
        Page page = element.getElement();
        try {
            page.moveTo(getElement(getParentPath(destination.getPathname()))
                .getElement(), getName(destination.getPathname()));
        } catch (DuplicatePageException ex) {
            throw (NamingException)new NamingException(
                "Destination path already exists: path="
                    + destination.getPath()).initCause(ex);
        } catch (LoopDetectedException ex) {
            throw new InvalidPathException("Loop detected: source path="
                + element.getPath() + ", destination path="
                + destination.getPath(), ex);
        }
    }


    @Override
    protected void setContent(PageElement element, String encoding,
        InputStream in)
        throws NamingException
    {
        if (TYPE_ATTACHED.equals(element.getType())) {
            // inはnullのはずだが、念のため。
            IOUtils.closeQuietly(in);
        } else {
            setPageContent(element, encoding, in);
        }
    }
}
