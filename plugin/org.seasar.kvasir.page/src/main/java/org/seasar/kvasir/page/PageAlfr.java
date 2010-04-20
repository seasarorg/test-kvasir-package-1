package org.seasar.kvasir.page;

import java.io.File;

import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;


/**
 * ページを管理するためのインタフェースです。
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


    /**
     * 指定されたキーに対応する{@link PageType}オブジェクトを返します。
     * <p>このメソッドは{@link PagePlugin#getPageType(Object)}と同じです。
     * </p>
     * 
     * @param key キー。
     * @return PageTypeオブジェクト。
     */
    PageType getPageType(Object key);


    /**
     * 指定されたキーに対応する{@link PageType}オブジェクトを返します。
     * <p>このメソッドは{@link PagePlugin#getPageType(Class)}と同じです。
     * </p>
     * 
     * @param key キー。
     * @return PageTypeオブジェクト。
     */
    <T extends PageType> T getPageType(Class<T> key);


    /**
     * 指定されたIDを持つPageオブジェクトを返します。
     * <p>指定されたIDを持つPageが見つからない場合はnullを返します。
     * </p>
     * 
     * @param id ID。nullを指定してはいけません。
     * @return Pageオブジェクト。
     */
    Page getPage(int id);


    /**
     * 指定されたIDを持つPageオブジェクトを返します。
     * <p>指定されたIDを持つPageが見つからない場合はnullを返します。
     * </p>
     * <p>Pageが存在した場合でも、指定されたクラスのインスタンスでない場合はnullを返します。
     * </p>
     * 
     * @param id ID。nullを指定してはいけません。
     * @param clazz Pageクラス。
     * @return Pageオブジェクト。
     */
    <P extends Page> P getPage(Class<P> clazz, int id);


    /**
     * 指定されたHeimに属するページで指定されたパス名のものを返します。
     * <p>存在しない場合はnullを返します。
     * </p>
     * <p>パス名は絶対パスで指定して下さい。また末尾に余計な「/」などをつけないようにして下さい。
     * ルートページを指定する場合は空文字列を指定して下さい。
     * </p>
     * 
     * @param heimId HeimのID。
     * @param pathname パス名。
     * nullを指定してはいけません。
     * @return ページ。
     * @see PathId#HEIM_MIDGARD
     * @see PathId#HEIM_ALFHEIM
     */
    Page getPage(int heimId, String pathname);


    /**
     * 指定されたHeimに属するページで指定されたパス名のものを返します。
     * <p>存在しない場合はnullを返します。
     * また、指定されたパス名のページが指定されたクラスにキャストできない場合もnullを返します。
     * </p>
     * <p>パス名は絶対パスで指定して下さい。また末尾に余計な「/」などをつけないようにして下さい。
     * ルートページを指定する場合は空文字列を指定して下さい。
     * </p>
     *
     * @param clazz 取得するページのクラス。
     * @param heimId HeimのID。
     * @param pathname パス名。
     * nullを指定してはいけません。
     * @return ページ。
     * @see PathId#HEIM_MIDGARD
     * @see PathId#HEIM_ALFHEIM
     */
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
