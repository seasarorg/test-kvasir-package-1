package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.naming.resources.Resource;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.cms.webdav.WebdavPlugin;
import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.ElementEntry;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;
import org.seasar.kvasir.cms.webdav.naming.InvalidPathException;
import org.seasar.kvasir.cms.webdav.setting.WebdavPluginSettings;


public class ElementDirContext<T> extends DirContextBase<T>
{
    protected ElementFactory<T>[] elementFactories_;

    protected WebdavPlugin plugin_;


    /*
     * constructors
     */

    public ElementDirContext(Hashtable<?, ?> env,
        ElementFactory<T>[] elementFactories, WebdavPlugin plugin)
    {
        super(env);
        elementFactories_ = elementFactories;
        for (int i = 0; i < elementFactories.length; i++) {
            elementFactories[i].setDirContext(this);
        }
        plugin_ = plugin;
    }


    /*
     * public scope methods
     */

    public void close()
        throws NamingException
    {
        super.close();
        elementFactories_ = null;
        plugin_ = null;
    }


    /*
     * protected scope methods
     */

    protected DirContextBase<T> newInstance(Hashtable<?, ?> env)
    {
        return new ElementDirContext<T>(env, elementFactories_, plugin_);
    }


    protected NameParser nameParserInstance()
    {
        return new ElementNameParser();
    }


    protected Resource newElementResource(Element<T> element)
    {
        return new ElementResource<T>(element);
    }


    protected Attributes newElementResourceAttributes(Element<T> element)
    {
        return new ElementResourceAttributes<T>(element);
    }


    protected void createElement(Element<T> element, InputStream in)
        throws NamingException
    {
        try {
            element.create(getCharset(element), in);
        } catch (InvalidPathException ex) {
            // なぜかWebdavServlet#doMove()が呼び出しているdeleteResource()
            // で呼び出されるsendError(SC_NOT_FOUND)がクライアントに返されない
            // またはクライアント（WebDrive）でエラーと見なされないので
            // こうしている。
            throw (IllegalArgumentException)new IllegalArgumentException()
                .initCause(ex);
        } catch (NamingException ex) {
            throw ex;
        } catch (Throwable t) {
            throw (NamingException)new NamingException().initCause(t);
        }
    }


    protected String getCharset(Element<T> element)
    {
        WebdavPluginSettings settings = plugin_.getSettings();
        String charset = settings.findCharset(element.getPath());
        if (charset == null) {
            charset = System.getProperty("file.encoding");
        }
        return charset;
    }


    protected void modifyElement(Element<T> element, InputStream in)
        throws NamingException
    {
        try {
            element.setContent(getCharset(element), in);
        } catch (Throwable t) {
            throw (NamingException)new NamingException().initCause(t);
        }
    }


    protected void deleteElement(Element<T> element)
        throws NamingException
    {
        try {
            element.delete();
        } catch (NamingException ex) {
            throw ex;
        } catch (Throwable t) {
            throw (NamingException)new NamingException().initCause(t);
        }
    }


    protected void moveElement(Element<T> element, Element<T> destination)
        throws NamingException
    {
        try {
            element.move(destination);
        } catch (NamingException ex) {
            throw ex;
        } catch (Throwable t) {
            throw (NamingException)new NamingException().initCause(t);
        }
    }


    public Element<T> newElement(String path)
        throws NamingException
    {
        try {
            for (int i = elementFactories_.length - 1; i >= 0; i--) {
                Element<T> element = elementFactories_[i].newInstance(path);
                if (element != null) {
                    return element;
                }
            }
            return null;
        } catch (InvalidPathException ex) {
            // なぜかWebdavServlet#doMove()が呼び出しているdeleteResource()
            // で呼び出されるsendError(SC_NOT_FOUND)がクライアントに返されない
            // またはクライアント（WebDrive）でエラーと見なされないので
            // こうしている。
            throw (IllegalArgumentException)new IllegalArgumentException()
                .initCause(ex);
        } catch (Throwable t) {
            throw (NamingException)new NamingException().initCause(t);
        }
    }


    protected Iterator<ElementEntry> getElementIterator(Element<T> element)
        throws NamingException
    {
        List<ElementEntry> list = new ArrayList<ElementEntry>();
        String[] childPaths = getChildPaths(element);
        for (int i = 0; i < childPaths.length; i++) {
            Element<T> childEi = newElement(childPaths[i]);
            list
                .add(new ElementEntry(childEi.getName(), childEi.isCollection()));
        }

        return list.iterator();
    }


    protected String[] getChildPaths(Element<T> element)
        throws NamingException
    {
        Set<String> set = new LinkedHashSet<String>();
        for (int i = elementFactories_.length - 1; i >= 0; i--) {
            String[] paths = elementFactories_[i].getChildPaths(element);
            for (int j = 0; j < paths.length; j++) {
                set.add(paths[j]);
            }
        }
        return set.toArray(new String[0]);
    }


    public int getHeimId()
    {
        return CmsUtils.getHeimId();
    }


    public String getParentPath(String path)
    {
        if (path == null) {
            return null;
        }

        int slash = path.lastIndexOf("/");
        if (slash >= 0) {
            return path.substring(0, slash);
        } else {
            return null;
        }
    }


    public String getName(String path)
    {
        int slash = path.lastIndexOf("/");
        if (slash >= 0) {
            return path.substring(slash + 1);
        } else {
            return path;
        }
    }
}
