package org.seasar.kvasir.page.impl;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.LinkedHashSet;

import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.page.CreatedPageEvent;
import org.seasar.kvasir.page.DeletedPageEvent;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.LordChangedPageEvent;
import org.seasar.kvasir.page.MovedPageEvent;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageListener;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.UpdatedPageEvent;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.condition.PageConditionWithHeimId;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageAlfrImpl
    implements PageAlfr, Lifecycle
{
    private PagePlugin plugin_;

    private boolean started_ = false;

    private PageCache cache_;


    public void setPlugin(PagePlugin plugin)
    {
        plugin_ = plugin;
    }


    /*
     * Lifecycle
     */

    public boolean start()
    {
        if (started_) {
            return true;
        }

        // PageCacheをDIしていないのは、PageAlfrが他のコンポーネントにDIされるタイミングが
        // 必ずしもDBの初期化よりも後ではないから。DBの初期化の後にPageCacheを取得することで、
        // PageDaoが正しく取得できる。
        cache_ = plugin_.getComponentContainer().getComponent(PageCache.class);

        started_ = true;

        return true;
    }


    public void stop()
    {
        if (!started_) {
            return;
        }

        cache_ = null;

        plugin_ = null;

        started_ = false;
    }


    /*
     * PageAlfr
     */

    public PageType getPageType(Object key)
    {
        return plugin_.getPageType(key);
    }


    public <T extends PageType> T getPageType(Class<T> key)
    {
        return plugin_.getPageType(key);
    }


    public Page getPage(int id)
    {
        return newPage(cache_.getPageDto(id));
    }


    @SuppressWarnings("unchecked")
    public <P extends Page> P getPage(Class<P> clazz, int id)
    {
        Page page = getPage(id);
        if ((page != null) && !clazz.isAssignableFrom(page.getClass())) {
            page = null;
        }
        return (P)page;
    }


    public Page getPage(int heimId, String pathname)
    {
        return newPage(cache_.getPageDto(heimId, pathname));
    }


    @SuppressWarnings("unchecked")
    public <P extends Page> P getPage(Class<P> clazz, int heimId,
        String pathname)
    {
        Page page = getPage(heimId, pathname);
        if ((page != null) && !clazz.isAssignableFrom(page.getClass())) {
            page = null;
        }
        return (P)page;
    }


    public Page findNearestPage(final int heimId, final String pathname)
    {
        if (!pathname.startsWith("/")) {
            return getRootPage(heimId);
        }

        Page page;
        String path = pathname;
        while ((page = getPage(heimId, path)) == null) {
            int slash = path.lastIndexOf('/');
            if (slash < 0) {
                break;
            }
            path = path.substring(0, slash);
        }
        return page;
    }


    public Page findPage(int heimId, String basePathname, String subPathname)
    {
        return getPage(heimId, cache_.findPathname(heimId, basePathname,
            subPathname));
    }


    public Page[] getPages(int[] ids)
    {
        return newPages(cache_.getPageDtos(ids), null);
    }


    public Page[] getPages(Number[] ids)
    {
        return newPages(cache_.getPageDtos(ids), null);
    }


    Page[] newPages(PageDto[] dtos, String type)
    {
        Page[] pages;
        if (type != null) {
            pages = (Page[])Array.newInstance(getPageType(type).getInterface(),
                dtos.length);
        } else {
            pages = new Page[dtos.length];
        }
        for (int i = 0; i < pages.length; i++) {
            pages[i] = newPage(dtos[i]);
        }
        return pages;
    }


    public Page[] getPages(int heimId, PageCondition cond)
    {
        return newPages(cache_.getPageDtos(new PageConditionWithHeimId(cond,
            heimId)), cond.getType());
    }


    public int getPagesCount(int heimId, PageCondition cond)
    {
        return cache_.getPagesCount(new PageConditionWithHeimId(cond, heimId));
    }


    public Page getRootPage(int heimId)
    {
        return getPage(heimId, "");
    }


    public User getAdministrator()
    {
        return getPage(User.class, Page.ID_ADMINISTRATOR_USER);
    }


    public Page[] getChildPages(Page page)
    {
        return getChildPages(page, new PageCondition());
    }


    public Page[] getChildPages(final Page page, final PageCondition cond)
    {
        return newPages(cache_.getChildPageDtos(page.getDto(), cond), cond
            .getType());
    }


    public String[] getChildPageNames(Page page)
    {
        return getChildPageNames(page, new PageCondition());
    }


    public String[] getChildPageNames(Page page, PageCondition cond)
    {
        return cache_.getChildPageNames(page.getDto(), cond);
    }


    public int[] getChildPageIds(Page page)
    {
        return getChildPageIds(page, new PageCondition());
    }


    public int[] getChildPageIds(final Page page, final PageCondition cond)
    {
        return cache_.getChildPageIds(page.getDto(), cond);
    }


    public int getChildPagesCount(Page page)
    {
        return getChildPagesCount(page, new PageCondition());
    }


    public int getChildPagesCount(final Page page, final PageCondition cond)
    {
        return cache_.getChildPageCount(page.getDto(), cond);
    }


    public Page createChildPage(final Page parent, final PageMold mold)
        throws DuplicatePageException
    {
        Page child = newPage(cache_.createChildPageDto(parent.getDto(), mold));

        // ページに関する初期化があれば実行する。
        plugin_.getPageType(child.getType()).processAfterCreated(child, mold);

        // abilityAlfr独自の初期化があれば実行する。
        if (!mold.isOmitCreationProcessForAbilityAlfr()) {
            PageAbilityAlfr[] alfrs = plugin_.getPageAbilityAlfrs();
            for (int i = 0; i < alfrs.length; i++) {
                alfrs[i].create(child);
            }
        }

        // リスナに通知する。
        PageListener[] pageListeners = plugin_.getPageListeners();
        for (int i = 0; i < pageListeners.length; i++) {
            pageListeners[i].notifyChanged(new CreatedPageEvent(child));
        }

        return child;
    }


    public Page createRootPage(int heimId)
        throws DuplicatePageException
    {
        return newPage(cache_.createRootPageDto(heimId));
    }


    public boolean deletePage(final Page page)
    {
        if (page.isRoot()
            && (page.getHeimId() == PathId.HEIM_MIDGARD || page.getHeimId() == PathId.HEIM_ALFHEIM)) {
            return false;
        }

        Page[] children = page.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].delete();
        }

        if (runWithLocking(new Page[] { page }, new Processable<Boolean>() {
            public Boolean process()
                throws ProcessableRuntimeException
            {
                if (page.getChildrenCount() > 0) {
                    return Boolean.FALSE;
                }
                PageAbilityAlfr[] alfrs = plugin_.getPageAbilityAlfrs();
                for (int i = 0; i < alfrs.length; i++) {
                    alfrs[i].delete(page);
                }

                plugin_.getPageType(page.getType()).processBeforeDeleting(page);

                cache_.releasePages(page.getDto());
                cache_.deletePageDto(page.getDto());

                return Boolean.TRUE;
            }
        }).booleanValue()) {
            // リスナに通知する。
            plugin_.notifyPageListeners(new DeletedPageEvent(page.getId(), page
                .getHeimId(), page.getPathname()));
            return true;
        } else {
            return false;
        }
    }


    public int[] getPageIds(PageCondition cond)
    {
        return cache_.getPageIds(cond);
    }


    public void touch(Page[] pages)
    {
        touch(pages, true);
    }


    public void touch(Page[] pages, boolean updateModifyDate)
    {
        if (pages.length == 1) {
            pages[0].touch(updateModifyDate);
        } else if (pages.length > 1) {
            if (updateModifyDate) {
                Date modifyDate = new Date();
                for (int i = 0; i < pages.length; i++) {
                    pages[i].setModifyDate(modifyDate);
                }
            }

            // リスナに通知する。
            plugin_.notifyPageListeners(new UpdatedPageEvent(pages));
        }
    }


    Page newPage(PageDto dto)
    {
        if (dto == null) {
            return null;
        } else {
            return wrapPage(new PageImpl(plugin_, this, dto));
        }
    }


    Page wrapPage(Page page)
    {
        if (page == null) {
            return null;
        } else {
            return plugin_.getPageType(page.getType()).wrapPage(page);
        }
    }


    public void setAsLord(final Page page, final boolean set)
    {
        if (page.isRoot()) {
            // ルートページのLordは変更できない。
            if (set) {
                return;
            } else {
                throw new IllegalArgumentException(
                    "Can't change lord property of root page");
            }
        }

        Page lord = page.getLord();
        int oldLordId = lord.getId();
        int newLordId;
        if (page.equals(lord)) {
            if (set) {
                // 既にlordなので何もしない。
                return;
            }
            newLordId = page.getParent().getLord().getId();
        } else {
            if (!set) {
                // もともとlordでないので何もしない。
                return;
            }
            newLordId = page.getId();
        }

        cache_.changeLordId(page.getDto(), oldLordId, newLordId);
        refreshPage(page);

        // リスナに通知する。
        plugin_.notifyPageListeners(new LordChangedPageEvent(page.getHeimId(),
            page.getPathname(), oldLordId, newLordId));
    }


    public void moveTo(final Page from, final Page toParent, final String toName)
        throws DuplicatePageException, LoopDetectedException
    {
        if (from.isRoot()) {
            // ルートページは移動できない。
            throw new IllegalArgumentException("Can't move root page");
        }
        final int heimId = from.getHeimId();
        if (heimId != toParent.getHeimId()) {
            // 違うHeimには移動できない。
            throw new IllegalArgumentException(
                "Can't move beyond heim: from heimId=" + heimId
                    + ", to heimId=" + toParent.getHeimId());
        }

        final String actualToName;
        if (toName == null) {
            actualToName = from.getName();
        } else {
            actualToName = toName;
        }

        Page to = toParent.getChild(actualToName);
        if (to != null) {
            // 移動先が既に存在する。
            if (to.equals(this)) {
                // 自分自身への移動。
                return;
            } else {
                throw new DuplicatePageException("Destination already exists: "
                    + to.getPathname());
            }
        }

        String fromPathname = from.getPathname();
        String toParentPathname = toParent.getPathname();
        if (PageUtils.isLooped(fromPathname, toParentPathname)) {
            // ループしている。
            throw new LoopDetectedException("Can't move " + fromPathname
                + " into " + toParentPathname);
        }

        cache_.moveTo(from.getHeimId(), from.getDto(), toParent.getDto(),
            actualToName);
        refreshPage(from);

        // リスナに通知する。
        plugin_.notifyPageListeners(new MovedPageEvent(heimId, fromPathname,
            wrapPage(from)));
    }


    public int updatePage(Page page, PageDto dto)
    {
        int result = cache_.updatePageDto(page.getDto(), dto);
        if (result > 0) {
            refreshPage(page);
        }
        return result;
    }


    public void refreshPage(Page page)
    {
        page.setDto(cache_.refresh(page.getDto()));
    }


    public <R> R runWithLocking(Page[] pages, Processable<R> processable)
        throws ProcessableRuntimeException
    {
        return cache_.runWithLocking(toIdArray(pages), processable);
    }


    Integer[] toIdArray(Page[] pages)
    {
        // 重複を排除するようにしておく。（cf. [#KVASIR-169]）
        LinkedHashSet<Integer> set = new LinkedHashSet<Integer>();
        for (int i = 0; i < pages.length; i++) {
            set.add(pages[i].getDto().getId());
        }
        return set.toArray(new Integer[0]);
    }
}
