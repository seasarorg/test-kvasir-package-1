package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.naming.NamingContextBindingsEnumeration;
import org.apache.naming.NamingContextEnumeration;
import org.apache.naming.NamingEntry;
import org.apache.naming.resources.Resource;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.ElementEntry;


public abstract class DirContextBase<T>
    implements DirContext
{
    private static NameParser parser_ = null;

    private Hashtable<?, ?> env_ = null;

    private String basePath_ = null;

    private String cwd_ = "/";

    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * abstract methods
     */

    abstract protected DirContextBase<T> newInstance(Hashtable<?, ?> env);


    abstract protected NameParser nameParserInstance();


    abstract protected Resource newElementResource(Element<T> element);


    abstract protected Attributes newElementResourceAttributes(
        Element<T> element);


    abstract protected void createElement(Element<T> ei, InputStream element)
        throws NamingException;


    abstract protected void modifyElement(Element<T> ei, InputStream element)
        throws NamingException;


    abstract protected void deleteElement(Element<T> element)
        throws NamingException;


    abstract protected void moveElement(Element<T> element,
        Element<T> destination)
        throws NamingException;


    abstract protected Element<T> newElement(String path)
        throws NamingException;


    abstract protected Iterator<ElementEntry> getElementIterator(
        Element<T> element)
        throws NamingException;


    /*
     * constructors
     */

    protected DirContextBase()
    {
        this(null);
    }


    protected DirContextBase(Hashtable<?, ?> env)
    {
        if (env == null) {
            env_ = null;
        } else {
            env_ = (Hashtable<?, ?>)env.clone();
        }
    }


    /*
     * static methods
     */

    protected static InputStream getInputStream(Object obj)
        throws NamingException
    {
        InputStream in = null;
        if (obj instanceof Resource) {
            try {
                in = ((Resource)obj).streamContent();
            } catch (IOException ex) {
                ;
            }
        } else if (obj instanceof InputStream) {
            in = (InputStream)obj;
        }
        if (in == null) {
            throw new NamingException("Can't get input stream");
        }

        return in;
    }


    /*
     * public scope methods
     */

    public final void setCwd(String cwd)
    {
        if (cwd.endsWith("/")) {
            cwd_ = cwd;
        } else {
            cwd_ = cwd + "/";
        }
    }


    public Object lookup(String name)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("lookup: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.canRead()) {
            throw new NameNotFoundException(name + " not found");
        }

        if (ei.isCollection()) {
            DirContextBase<T> dcb = newInstance(env_);
            dcb.setCwd(name);
            return dcb;
        } else {
            return newElementResource(ei);
        }
    }


    public Object lookup(Name name)
        throws NamingException
    {
        return lookup(name.toString());
    }


    public void bind(String name, Object obj)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("bind: " + name);
        }

        bind0(name, obj, getElement(name));
    }


    public void bind(Name name, Object obj)
        throws NamingException
    {
        bind(name.toString(), obj);
    }


    public void rebind(String name, Object obj)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("rebind: " + name);
        }

        bind0(name, obj, getElement(name));
    }


    protected void bind0(String name, Object obj, Element<T> element)
        throws NamingException
    {
        if (element.canCreate()) {
            createElement(element, DirContextBase.getInputStream(obj));
        } else if (!element.canRead()) {
            throw new NameNotFoundException(name + " not found");
        } else if (element.canModify() && !element.isCollection()) {
            modifyElement(element, DirContextBase.getInputStream(obj));
        } else {
            throw new InvalidNameException("Cannot rebind: " + name);
        }
    }


    public void rebind(Name name, Object obj)
        throws NamingException
    {
        rebind(name.toString(), obj);
    }


    public void unbind(String name)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("unbind: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.canRead()) {
            log_.debug("cant read");
            return;
        } else if (!ei.canDelete()) {
            log_.debug("cant delete");
            throw new InvalidNameException("Cannot unbind: " + name);
        }

        log_.debug("deleteElement CALLING...");
        deleteElement(ei);
    }


    public void unbind(Name name)
        throws NamingException
    {
        unbind(name.toString());
    }


    public void rename(String oldname, String newname)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("rename: " + oldname + " to " + newname);
        }

        Element<T> oldei = getElement(oldname);
        Element<T> newei = getElement(newname);
        if (!oldei.canMove() || !newei.canCreate()) {
            throw new InvalidNameException("Cannot rename");
        }

        moveElement(oldei, newei);
    }


    public void rename(Name oldname, Name newname)
        throws NamingException
    {
        rename(oldname.toString(), newname.toString());
    }


    @SuppressWarnings("unchecked")
    public NamingEnumeration<NameClassPair> list(String name)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("list: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.canRead()) {
            throw new NameNotFoundException(name + " not found");
        } else if (!ei.isCollection()) {
            throw new NotContextException(name + " cannot be listed");
        }

        return new NamingContextEnumeration(listElement(ei).iterator());
    }


    public NamingEnumeration<NameClassPair> list(Name name)
        throws NamingException
    {
        return list(name.toString());
    }


    @SuppressWarnings("unchecked")
    public NamingEnumeration<Binding> listBindings(String name)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("listBindings: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.canRead()) {
            throw new NameNotFoundException(name + " not found");
        } else if (!ei.isCollection()) {
            throw new NotContextException(name + " cannot be listed");
        }

        return new NamingContextBindingsEnumeration(listElement(ei).iterator(),
            this);
    }


    public NamingEnumeration<Binding> listBindings(Name name)
        throws NamingException
    {
        return listBindings(name.toString());
    }


    public void destroySubcontext(String name)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("destroySubcontext: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.isCollection()) {
            throw new NotContextException(name + " cannot be destroyed");
        } else if (!ei.canDelete()) {
            throw new InvalidNameException("Cannot destroy: " + name);
        }

        deleteElement(ei);
    }


    public void destroySubcontext(Name name)
        throws NamingException
    {
        destroySubcontext(name.toString());
    }


    public Context createSubcontext(String name)
        throws NamingException
    {
        return createSubcontext(name, null);
    }


    public Context createSubcontext(Name name)
        throws NamingException
    {
        return createSubcontext(name.toString());
    }


    public Object lookupLink(String name)
        throws NamingException
    {
        return lookup(name);
    }


    public Object lookupLink(Name name)
        throws NamingException
    {
        return lookupLink(name.toString());
    }


    public NameParser getNameParser(String name)
        throws NamingException
    {
        if (parser_ == null) {
            synchronized (getClass()) {
                if (parser_ == null) {
                    parser_ = nameParserInstance();
                }
            }
        }

        return parser_;
    }


    public NameParser getNameParser(Name name)
        throws NamingException
    {
        return getNameParser(name.toString());
    }


    public String composeName(String name, String prefix)
        throws NamingException
    {
        Name result = composeName(parser_.parse(name), parser_.parse(prefix));
        return result.toString();
    }


    public Name composeName(Name name, Name prefix)
        throws NamingException
    {
        Name result = (Name)(prefix.clone());
        result.addAll(name);
        return result;
    }


    @SuppressWarnings("unchecked")
    public Object addToEnvironment(String propName, Object propVal)
        throws NamingException
    {
        if (env_ == null) {
            env_ = new Hashtable<Object, Object>();
        }
        return ((Hashtable<Object, Object>)env_).put(propName, propVal);
    }


    public Object removeFromEnvironment(String propName)
        throws NamingException
    {
        if (env_ == null) {
            return null;
        }

        return env_.remove(propName);
    }


    public Hashtable<?, ?> getEnvironment()
        throws NamingException
    {
        if (env_ == null) {
            // Must return non-null
            return new Hashtable<Object, Object>();
        } else {
            return (Hashtable<?, ?>)env_.clone();
        }
    }


    public String getNameInNamespace()
        throws NamingException
    {
        return "";
    }


    public void close()
        throws NamingException
    {
        env_ = null;
    }


    public Attributes getAttributes(Name name)
        throws NamingException
    {
        return getAttributes(name, null);
    }


    public Attributes getAttributes(String name)
        throws NamingException
    {
        return getAttributes(name, null);
    }


    public Attributes getAttributes(Name name, String[] attrIds)
        throws NamingException
    {
        return getAttributes(name.toString(), attrIds);
    }


    public Attributes getAttributes(String name, String[] attrIds)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("getAttributes: " + name);
        }

        Element<T> ei = getElement(name);
        if (!ei.canRead()) {
            throw new NameNotFoundException(name + " not found");
        }

        return newElementResourceAttributes(ei);
    }


    public void modifyAttributes(Name name, int mod_op, Attributes attrs)
        throws NamingException
    {
        modifyAttributes(name.toString(), mod_op, attrs);
    }


    public void modifyAttributes(String name, int mod_op, Attributes attrs)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public void modifyAttributes(Name name, ModificationItem[] mods)
        throws NamingException
    {
        modifyAttributes(name.toString(), mods);
    }


    public void modifyAttributes(String name, ModificationItem[] mods)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public void bind(Name name, Object obj, Attributes attrs)
        throws NamingException
    {
        if (attrs == null) {
            bind(name, obj);
        } else {
            throw new OperationNotSupportedException();
        }
    }


    public void bind(String name, Object obj, Attributes attrs)
        throws NamingException
    {
        if (attrs == null) {
            bind(name, obj);
        } else {
            throw new OperationNotSupportedException();
        }
    }


    public void rebind(Name name, Object obj, Attributes attrs)
        throws NamingException
    {
        if (attrs == null) {
            rebind(name, obj);
        } else {
            throw new OperationNotSupportedException();
        }
    }


    public void rebind(String name, Object obj, Attributes attrs)
        throws NamingException
    {
        if (attrs == null) {
            rebind(name, obj);
        } else {
            throw new OperationNotSupportedException();
        }
    }


    public DirContext createSubcontext(Name name, Attributes attrs)
        throws NamingException
    {
        return createSubcontext(name.toString(), attrs);
    }


    public DirContext createSubcontext(String name, Attributes attrs)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("createSubcontext: " + name);
        }

        Element<T> element = getElement(name);
        if (element.canCreate() && element.isCollection()) {
            createElement(element, null);
        } else if (element.canRead()) {
            // 既にある場合は何もしない。
        } else {
            throw new NamingException("Cannot create: " + name);
        }
        return (DirContext)lookup(name);
    }


    public DirContext getSchema(Name name)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public DirContext getSchema(String name)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public DirContext getSchemaClassDefinition(Name name)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public DirContext getSchemaClassDefinition(String name)
        throws NamingException
    {
        throw new OperationNotSupportedException();
    }


    public NamingEnumeration<SearchResult> search(Name name,
        Attributes matchingAttributes, String[] attributesToReturn)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(String name,
        Attributes matchingAttributes, String[] attributesToReturn)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(Name name,
        Attributes matchingAttributes)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(String name,
        Attributes matchingAttributes)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(Name name, String filter,
        SearchControls cons)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(String name, String filter,
        SearchControls cons)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
        Object[] filterArgs, SearchControls cons)
        throws NamingException
    {
        return null;
    }


    public NamingEnumeration<SearchResult> search(String name,
        String filterExpr, Object[] filterArgs, SearchControls cons)
        throws NamingException
    {
        return null;
    }


    /*
     * protected scope methods
     */

    protected Element<T> getElement(String name)
        throws NamingException
    {
        if (name.length() > 0) {
            if (!name.startsWith("/")) {
                if (cwd_ == null) {
                    name = "/" + name;
                } else {
                    name = cwd_ + name;
                }
            }
            if (name.endsWith("/")) {
                name = name.substring(0, name.length() - 1);
            }
        }

        return newElement(name);
    }


    protected List<NamingEntry> listElement(Element<T> ei)
        throws NamingException
    {
        if (log_.isDebugEnabled()) {
            log_.debug("listElement: " + ei.getPath());
        }

        List<NamingEntry> entries = new ArrayList<NamingEntry>();
        NamingEntry entry = null;

        Iterator<ElementEntry> itr = getElementIterator(ei);
        while (itr.hasNext()) {
            ElementEntry ee = itr.next();
            String name = ee.getName();
            if (ee.isCollection()) {
                DirContextBase<T> dcb = newInstance(env_);
                dcb.setCwd(ei.getPath() + "/" + name);
                entry = new NamingEntry(name, dcb, NamingEntry.ENTRY);
                entries.add(entry);
            } else {
                entry = new NamingEntry(name, newElementResource(newElement(ei
                    .getPath()
                    + "/" + name)), NamingEntry.ENTRY);
                entries.add(entry);
            }
        }

        if (log_.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Entries:");
            int size = entries.size();
            for (int i = 0; i < size; i++) {
                sb.append(" ");
                sb.append(entries.get(i).name);
            }
            log_.debug(sb.toString());
        }

        return entries;
    }
}
