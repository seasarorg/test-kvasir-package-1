package org.seasar.kvasir.page;

import java.io.File;

import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageAlfr
    extends PageAlfrInternal
{
    String DIR_PROPERTIES = File.separator + "properties";

    String DIR_CONTENTS = File.separator + "contents";

    String DIR_TEMPLATES = File.separator + "templates";


    PageType getPageType(Object key);


    <T extends PageType> T getPageType(Class<T> key);


    Page getPage(int id);


    <P extends Page> P getPage(Class<P> clazz, int id);


    Page getPage(int heimId, String pathname);


    <P extends Page> P getPage(Class<P> clazz, int heimId, String pathname);


    /**
     * 指定されたパス名に対応するページ、
     * もしくは指定されたパス名に最も近い祖先ページを返します。
     * <p>パス名が例えば<code>/path/to/page</code>である場合、
     * このメソッドは<code>/path/to/page</code>、<code>/path/to</code>、
     * <code>/path</code>、ルートページのパス名の順に
     * {@link #getPage(String)}を呼び出し、
     * ページが見つかった時点で処理を終了し、
     * 見つかったページを返します。
     *
     * @param heim 所属するheim。
     * @param pathname パス名。
     * @return ページオブジェクト。
     */
    Page findNearestPage(int heimId, String pathname);


    Page findPage(int heimId, String basePathname, String subPathname);


    Page[] getPages(int[] ids);


    Page[] getPages(Number[] ids);


    Page[] getPages(int heimId, PageCondition cond);


    Page getRootPage(int heimId);


    User getAdministrator();


    Page[] getChildPages(Page page);


    Page[] getChildPages(Page page, PageCondition cond);


    String[] getChildPageNames(Page page);


    String[] getChildPageNames(Page page, PageCondition cond);


    int[] getChildPageIds(Page page);


    int[] getChildPageIds(Page page, PageCondition cond);


    int getChildPagesCount(Page page);


    int getChildPagesCount(Page page, PageCondition cond);


    Page createChildPage(Page parent, PageMold mold)
        throws DuplicatePageException;


    boolean deletePage(Page page);


    int[] getPageIds(PageCondition cond);


    void touch(Page[] pages);


    void touch(Page[] pages, boolean updateModifyDate);


    void setAsLord(Page page, boolean set);


    void moveTo(Page from, Page toParent, String toName)
        throws DuplicatePageException, LoopDetectedException;


    void refreshPage(Page page);


    <R> R runWithLocking(Page[] pages, Processable<R> processable)
        throws ProcessableRuntimeException, PageNotFoundRuntimeException;


    Page createRootPage(int heimId)
        throws DuplicatePageException;
}
