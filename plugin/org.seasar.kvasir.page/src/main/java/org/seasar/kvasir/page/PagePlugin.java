package org.seasar.kvasir.page;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.util.io.Resource;


/**
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


    PageAlfr getPageAlfr();


    PageType getPageType(Object key);


    <T extends PageType> T getPageType(Class<T> key);


    PageType[] getPageTypes();


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
