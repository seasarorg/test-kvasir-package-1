package org.seasar.kvasir.page;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.util.io.Resource;


/**
 * org.seasar.kvasir.pageプラグインのエントリポイントを表わすインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PagePlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.page";

    String ID_PATH = ID.replace('.', '/');


    /**
     * ページを管理するための{@lik PageAlfr}オブジェクトを返します。
     * 
     * @return PageAlfrオブジェクト。
     */
    PageAlfr getPageAlfr();


    /**
     * 指定されたキーに対応する{@link PageType}オブジェクトを返します。
     * <p>キーとしては{@link Page}インタフェースのサブインタフェースと
     * ページタイプのID文字列と{@link PageType}の実装クラスを指定することができます。
     * </p>
     * <p>キーに対応するPageTypeオブジェクトが存在しない場合は「page」ページタイプに対応する
     * PageTypeオブジェクトを返します。
     * </p>
     * 
     * @param key キー。
     * @return PageTypeオブジェクト。
     */
    PageType getPageType(Object key);


    /**
     * 指定されたキーに対応する{@link PageType}オブジェクトを返します。
     * <p>キーとしては{@link PageType}の実装クラスを指定することができます。
     * </p>
     * <p>キーに対応するPageTypeオブジェクトが存在しない場合はnullを返します。
     * </p>
     * 
     * @param key キー。
     * @return PageTypeオブジェクト。
     */
    <T extends PageType> T getPageType(Class<T> key);


    /**
     * 登録されているすべての{@link PageType}を返します。
     * <p>nullが返されることはありません。
     * </p>
     * 
     * @return PageTypeの配列。
     */
    PageType[] getPageTypes();


    /**
     * 登録されているすべての{@link PageListener}を返します。
     * <p>nullが返されることはありません。
     * </p>
     * 
     * @return PageListenerの配列。
     */
    PageListener[] getPageListeners();


    void notifyPageListeners(PageEvent pageEvent);


    PageAbilityAlfr getPageAbilityAlfr(Object key);


    <T extends PageAbilityAlfr> T getPageAbilityAlfr(Class<T> key);


    PageAbilityAlfr[] getPageAbilityAlfrs();


    /**
     * 指定された名前がページの名前として適切かどうかを判定します。
     * 
     * @param name 名前。
     * @return ページの名前として適切かどうか。
     */
    boolean isValidName(String name);


    /**
     * 指定されたIDを持つgardを定義しているプラグインのPluginインスタンスを返します。
     * <p>見つからない場合はnullを返します。
     *
     * @param gardId gardのID。
     * @return Pluginインスタンス。
     */
    Plugin<?> getPlugin(String gardId);


    PageGard getPageGard(String gardId);


    PageGard[] getPageGards();


    Page imports(Page parent, String name, Resource dir)
        throws DuplicatePageException;


    void imports(Page page, Resource dir, boolean replace);


    void exports(Resource dir, Page page);


    Page install(Page parent, String name, String gardId)
        throws DuplicatePageException;


    boolean install(Page page, String gardId)
        throws DuplicatePageException;


    void upgrade(Page page)
        throws DuplicatePageException;


    void uninstall(Page page);


    Page copy(Page parent, String name, Page source, boolean adjustName)
        throws DuplicatePageException, LoopDetectedException;


    boolean isAlreadyIntalled(String gardId, int heimId);


    boolean isAlreadyIntalled(PageGard gard, int heimId);
}
