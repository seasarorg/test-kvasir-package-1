package org.seasar.kvasir.cms.webdav.naming.page;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.cms.webdav.WebdavPlugin;
import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.InvalidPathException;
import org.seasar.kvasir.cms.webdav.naming.impl.DirContextBase;
import org.seasar.kvasir.cms.webdav.naming.impl.ElementDirContext;
import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory.ResourceEntry;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.type.Directory;


public class PageElementDirContext extends ElementDirContext<Page>
{
    public static final String NAME_ROOT = "root";

    public static final String DIR_ROOT = "/" + NAME_ROOT;

    public static final String SUFFIX_ATTACHED = "$attached";

    private PageElementFactory[] pageElementFactories_;

    private PageAlfr pageAlfr_;


    public PageElementDirContext(Hashtable<?, ?> env,
        PageElementFactory[] pageElementFactories, WebdavPlugin plugin,
        PageAlfr pageAlfr)
    {
        super(env, pageElementFactories, plugin);
        pageElementFactories_ = pageElementFactories;
        pageAlfr_ = pageAlfr;
    }


    @Override
    protected DirContextBase<Page> newInstance(Hashtable<?, ?> env)
    {
        return new PageElementDirContext(env, pageElementFactories_, plugin_,
            pageAlfr_);
    }


    @Override
    public Element<Page> newElement(String path)
        throws NamingException
    {
        try {
            String pathname = null;
            ResourceEntry resource = null;
            if (path.length() == 0) {
                pathname = null;
                resource = analyzeResourceName(null);
            } else {
                if (!path.startsWith(DIR_ROOT)) {
                    throw new InvalidPathException("Invalid path: " + path);
                }
                String subpath = path.substring(DIR_ROOT.length());
                if (subpath.length() == 0 || subpath.charAt(0) != '/') {
                    String name = getName(path);
                    resource = analyzeResourceName(name);
                    if (resource != null) {
                        if (resource.getName().equals(NAME_ROOT)) {
                            pathname = "";
                        } else {
                            throw new InvalidPathException("Invalid path: "
                                + path);
                        }
                    }
                } else {
                    String name = getName(subpath);
                    resource = analyzeResourceName(name);
                    if (resource != null) {
                        pathname = getPathnameFromPath(getParentPath(subpath)
                            + "/" + resource.getName());
                    }
                }
            }
            if (resource == null) {
                return null;
            }

            Page page = (pathname != null ? pageAlfr_.getPage(getHeimId(),
                pathname) : null);
            return resource.getFactory().constructElement(path, pathname, page,
                resource);
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


    @ForTest
    Content getLatestDefaultContent(Page page)
    {
        return page.getAbility(ContentAbility.class).getLatestContent(
            Page.VARIANT_DEFAULT);
    }


    ResourceEntry analyzeResourceName(String name)
    {
        for (int i = pageElementFactories_.length - 1; i >= 0; i--) {
            ResourceEntry resource = pageElementFactories_[i]
                .analyzeResourceName(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }


    protected String getPathnameFromPath(String path)
    {
        if (path == null) {
            return null;
        }

        if (path.indexOf(SUFFIX_ATTACHED) < 0) {
            // 効率化のため。
            return path;
        }

        List<String> segmentList = new ArrayList<String>();
        int pre = 0;
        int idx;
        while ((idx = path.indexOf('/', pre)) >= 0) {
            String segment = path.substring(pre, idx);
            String name = getNameFromAttachedDirectoryName(segment);
            segmentList.add(name != null ? name : segment);
            pre = idx + 1;
        }
        String segment = path.substring(pre);
        String name = getNameFromAttachedDirectoryName(segment);
        segmentList.add(name != null ? name : segment);

        String delim = "";
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> itr = segmentList.iterator(); itr.hasNext();) {
            sb.append(delim).append(itr.next());
            delim = "/";
        }
        return sb.toString();
    }


    protected String getAttachedDirectoryName(String name)
    {
        if (name == null) {
            return null;
        } else {
            return name + SUFFIX_ATTACHED;
        }
    }


    protected String getNameFromAttachedDirectoryName(
        String attachedDirectoryName)
    {
        if (attachedDirectoryName == null) {
            return null;
        } else if (!attachedDirectoryName.endsWith(SUFFIX_ATTACHED)) {
            return null;
        } else {
            return attachedDirectoryName.substring(0, attachedDirectoryName
                .length()
                - SUFFIX_ATTACHED.length());
        }
    }


    @Override
    public String[] getChildPaths(Element<Page> element)
        throws NamingException
    {
        List<String> list = new ArrayList<String>();
        String parentPathname = null;
        String delimiter = null;
        Page[] children = new Page[0];
        if (element.getPath().length() == 0) {
            parentPathname = "";
            delimiter = "";
            children = new Page[] { pageAlfr_.getRootPage(element.getHeimId()) };
        } else if (element.exists() && element.isCollection()
            && element.getElement() != null) {
            Page page = element.getElement();
            if (page.getName().equals(element.getName())
                && Page.TYPE.equals(page.getType()) && page.isNode()) {
                // FIXME ディレクトリをDAVクライアントから追加する際に、先にfield.xpropertiesなどが
                // 追加されてしまうと、空のページ（type=page）が作成されてしまう。その後にディレクトリ
                // そのものが追加された時は上書きオペレーションになるのだが、それをハンドリングできるポイント
                // がない。（つまり、ディレクトリの上書きというオペレーションが発生した時に呼び出される
                // メソッドがない。）そのため、ディレクトリを追加したはずなのにできるのはページというおか
                // しな状態になってしまう。
                // 仕方ないので、こうやって矛盾を探し、矛盾していたら補正するようにしている。
                // ただ、これだと、意図的に「pageなんだけどnode」というページを作っている場合に、
                // それがディレクトリになってしまう。
                // 他にいい方法はないか？
                // field.xpropertiesとかによってpageが作られた場合はpropertyとかにその旨
                // 記録しておいて、補正すべきかをそれを見て決める？
                page.setType(Directory.TYPE);
            }
            parentPathname = page.getPathname();
            delimiter = "/";
            children = page.getChildren();
        }
        for (int i = 0; i < children.length; i++) {
            String[] names = getResourceNames(children[i]);
            for (int j = 0; j < names.length; j++) {
                list.add(DIR_ROOT + parentPathname + delimiter + names[j]);
            }
        }
        return list.toArray(new String[0]);
    }


    String[] getResourceNames(Page page)
    {
        Set<String> set = new LinkedHashSet<String>();
        for (int i = pageElementFactories_.length - 1; i >= 0; i--) {
            String[] paths = pageElementFactories_[i].getResourceNames(page);
            for (int j = 0; j < paths.length; j++) {
                set.add(paths[j]);
            }
        }
        return set.toArray(new String[0]);
    }
}
