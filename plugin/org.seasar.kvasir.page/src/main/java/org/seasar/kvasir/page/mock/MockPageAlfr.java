package org.seasar.kvasir.page.mock;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.mock.MockDirectory;
import org.seasar.kvasir.page.type.mock.MockUser;


/**
 * @author YOKOTA Takehiko
 */
public class MockPageAlfr
    implements PageAlfr
{
    private MockDirectory rootPage_ = (MockDirectory)new MockDirectory(
        Page.ID_ROOT, PathId.HEIM_MIDGARD, "").setRoot(true);

    private MockUser administratorUser_ = new MockUser(
        Page.ID_ADMINISTRATOR_USER, PathId.HEIM_MIDGARD,
        Page.PATHNAME_ADMINISTRATOR_USER).setAdministrator(true);

    private Map<PathId, Page> pageMap_ = new HashMap<PathId, Page>();

    private Map<Integer, Page> pageByIdMap_ = new HashMap<Integer, Page>();

    private Map<PathId, SortedSet<Page>> childrenMap_ = new HashMap<PathId, SortedSet<Page>>();

    private int maxId_ = 10000;

    private Map<PathId, Map<PageCondition, Page[]>> childrenForConditionMap_ = new HashMap<PathId, Map<PageCondition, Page[]>>();


    public MockPageAlfr()
    {
        register(rootPage_);
        register(administratorUser_);
    }


    public Page createChildPage(Page parent, PageMold mold)
        throws DuplicatePageException
    {
        return null;
    }


    public boolean deletePage(Page page)
    {
        return true;
    }


    public Page findNearestPage(int heimId, String pathname)
    {
        return null;
    }


    public Page findPage(int heimId, String basePathname, String subPathname)
    {
        return null;
    }


    public User getAdministrator()
    {
        return administratorUser_;
    }


    public String[] getChildPageNames(int heimId, String pathname)
    {
        Page[] children = getChildPages(heimId, pathname);
        String[] names = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            names[i] = children[i].getName();
        }
        return names;
    }


    public String[] getChildPageNames(int heimId, String pathname,
        PageCondition cond)
    {
        Page[] children = getChildPages(heimId, pathname, cond);
        String[] names = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            names[i] = children[i].getName();
        }
        return names;
    }


    public int[] getChildPageIds(int heimId, String pathname)
    {
        Page[] children = getChildPages(heimId, pathname);
        int[] ids = new int[children.length];
        for (int i = 0; i < children.length; i++) {
            ids[i] = children[i].getId();
        }
        return ids;
    }


    public int[] getChildPageIds(int heimId, String pathname, PageCondition cond)
    {
        Page[] children = getChildPages(heimId, pathname, cond);
        int[] ids = new int[children.length];
        for (int i = 0; i < children.length; i++) {
            ids[i] = children[i].getId();
        }
        return ids;
    }


    public Page[] getChildPages(int heimId, String pathname)
    {
        return childrenMap_.get(new PathId(heimId, pathname)).toArray(
            new Page[0]);
    }


    public Page[] getChildPages(int heimId, String pathname, PageCondition cond)
    {
        Map<PageCondition, Page[]> map = childrenForConditionMap_
            .get(new PathId(heimId, pathname));
        if (map != null) {
            Page[] children = map.get(cond);
            if (children != null) {
                return children;
            }
        }
        return new Page[0];
    }


    public int getChildPagesCount(int heimId, String pathname)
    {
        return getChildPages(heimId, pathname).length;
    }


    public int getChildPagesCount(int heimId, String pathname,
        PageCondition cond)
    {
        return getChildPages(heimId, pathname, cond).length;
    }


    public Page getPage(int id)
    {
        return pageByIdMap_.get(id);
    }


    public <P extends Page> P getPage(Class<P> clazz, int id)
    {
        return null;
    }


    public Page getPage(int heimId, String pathname)
    {
        return pageMap_.get(new PathId(heimId, pathname));
    }


    public <P extends Page> P getPage(Class<P> clazz, int heimId,
        String pathname)
    {
        return null;
    }


    public int[] getPageIds(PageCondition cond)
    {
        return null;
    }


    public PageType getPageType(Object key)
    {
        return null;
    }


    public <T extends PageType> T getPageType(Class<T> key)
    {
        return null;
    }


    public Page[] getPages(int[] ids)
    {
        return null;
    }


    public Page[] getPages(int heimId, PageCondition cond)
    {
        return null;
    }


    public Page getRootPage(int heimId)
    {
        if (heimId == PathId.HEIM_MIDGARD) {
            return rootPage_;
        } else {
            return null;
        }
    }


    public void touch(Page[] pages)
    {
    }


    public void touch(Page[] pages, boolean updateModifyDate)
    {
    }


    public PageDto getDto(Integer id)
    {
        return null;
    }


    public void moveTo(Page from, Page toParent, String toName)
        throws DuplicatePageException, LoopDetectedException
    {
    }


    public void setAsLord(Page page, boolean set)
    {
    }


    public void refreshPage(Page page)
    {
    }


    public int updatePage(Page page, PageDto changeSet)
    {
        return 0;
    }


    public Page[] getChildPages(Page page)
    {
        return null;
    }


    public Page[] getChildPages(Page page, PageCondition cond)
    {
        return null;
    }


    public String[] getChildPageNames(Page page)
    {
        return null;
    }


    public String[] getChildPageNames(Page page, PageCondition cond)
    {
        return null;
    }


    public int[] getChildPageIds(Page page)
    {
        return null;
    }


    public int[] getChildPageIds(Page page, PageCondition cond)
    {
        return null;
    }


    public int getChildPagesCount(Page page)
    {
        return 0;
    }


    public int getChildPagesCount(Page page, PageCondition cond)
    {
        return 0;
    }


    public <R> R runWithLocking(Page[] pages, Processable<R> processable)
        throws ProcessableRuntimeException, PageNotFoundRuntimeException
    {
        return null;
    }


    public Page[] getPages(Number[] ids)
    {
        return new Page[0];
    }


    public Page createRootPage(int heimId)
        throws DuplicatePageException
    {
        return null;
    }


    public void register(MockPage page)
    {
        if (page.getId() > maxId_) {
            maxId_ = page.getId();
        }

        int heimId = page.getHeimId();
        String pathname = page.getPathname();
        LinkedList<String> pathList = new LinkedList<String>();
        while ((pathname = PageUtils.getParentPathname(pathname)) != null) {
            pathList.addFirst(pathname);
        }
        for (Iterator<String> itr = pathList.iterator(); itr.hasNext();) {
            registerDirectory(heimId, itr.next());
        }
        register0(page);
    }


    protected void registerDirectory(int heimId, String pathname)
    {
        if (!pageMap_.containsKey(new PathId(heimId, pathname))) {
            register0(new MockDirectory(newId(), heimId, pathname));
        }
    }


    protected int newId()
    {
        return ++maxId_;
    }


    protected void register0(MockPage page)
    {
        page.setPageAlfr(this);

        pageMap_.put(new PathId(page.getHeimId(), page.getPathname()), page);
        pageByIdMap_.put(page.getId(), page);

        String parentPathname = page.getParentPathname();
        if (parentPathname != null) {
            PathId parentPathId = new PathId(page.getHeimId(), parentPathname);
            SortedSet<Page> children = childrenMap_.get(parentPathId);
            if (children == null) {
                children = new TreeSet<Page>(new Comparator<Page>() {
                    public int compare(Page o1, Page o2)
                    {
                        return o1.getOrderNumber() - o2.getOrderNumber();
                    }
                });
                childrenMap_.put(parentPathId, children);
            }
            children.add(page);

            if (page.getParent() == null) {
                page.setParent(getPage(page.getHeimId(), page
                    .getParentPathname()));
            }
        }
    }


    public void setChildrenForCondition(MockPage page,
        PageCondition pageCondition, Page[] children)
    {
        PathId pathId = new PathId(page);
        Map<PageCondition, Page[]> map = childrenForConditionMap_.get(pathId);
        if (map == null) {
            map = new HashMap<PageCondition, Page[]>();
            childrenForConditionMap_.put(pathId, map);
        }
        map.put(pageCondition, children);
    }
}
