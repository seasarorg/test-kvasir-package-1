package org.seasar.kvasir.cms.pop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PopPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.cms.pop";

    String ID_PATH = ID.replace('.', '/');

    String COMPONENTNAME_GENERICPOP = "genericPop";


    /**
     * 全てのPopElementの配列を返します。
     *
     * @return PopElementの配列。nullが返されることはありません。
     */
    PopElement[] getPopElements();


    /**
     * 指定されたgardで利用可能なPOPに関するPopElementの配列を返します。
     * <p>inlineプロパティがtrueであるPopElementは返されません。
     * </p>
     *
     * @param gardId gardのID。
     * @return PopElementの配列。nullが返されることはありません。
     */
    PopElement[] getPopElements(String gardId);


    PopElement getPopElement(Object key);


    PopContext newContext(Page containerPage, HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest);


    PopContext newContext(Page containerPage, HttpServletRequest request,
        HttpServletResponse response);


    void updatePanes(int heimId);


    Pane getPane(int heimId, String paneId);


    Pane getPane(int heimId, String paneId, boolean create);


    Pane[] getPanes(int heimId);


    void removePane(int heimId, String paneId);


    /**
     * 指定されたIDに対応するPopインスタンスを返します。
     * <p>このメソッドは<code>getPop(heim, key, instanceId, true)</code>と同じです。
     * </p>
     *
     * @param heim POPインスタンスが所属するHeim。
     * @param key キー。
     * @param instanceId インスタンスID。
     * @return Popインスタンス。
     */
    Pop getPop(int heimId, Object key, int instanceId);


    /**
     * 指定されたキーとインスタンスIDに対応するPopインスタンスを返します。
     * <p>キーとしては、POP ID、対応するPOPエレメントのID、
     * それらの末尾から「Pop」という文字列を取り除いた文字列
     * のいずれかを指定することができます。
     * 例えばメニューPOPのPOP IDが「org.seasar.kvasir.cms.pop.menuPop」で、
     * 対応するPOPエレメントのIDが「menuPop」である場合、
     * メニューPOPを取り出すためのキーとして指定できるのは次のいずれかです。
     * </p>
     * <ul>
     * <li><code>org.seasar.kvasir.cms.pop.menuPop</code></li>
     * <li>menuPop</li>
     * <li><code>org.seasar.kvasir.cms.pop.menu</code></li>
     * <li>menu</li>
     * </ul>
     * <p>対応するPopインスタンスが存在しない場合は、
     * 指定されたキーに対応するPOPエントリが存在しないのであればnullを返します。
     * POPエントリが存在する場合で、単にインスタンスIDに対応するインスタンスが存在しない場合、
     * <code>create</code>がtrueであればインスタンスを生成して返します。
     * </p>
     *
     * @param heim POPインスタンスが所属するHeim。
     * @param key キー。
     * @param instanceId インスタンスID。
     * @param create POPインスタンスが存在しない場合に生成するかどうか。
     * @return Popインスタンス。
     */
    Pop getPop(int heimId, Object key, int instanceId, boolean create);


    /**
     * 指定されたIDに対応するPopインスタンスを返します。
     * <p>このメソッドは<code>getPop(id, true)</code>と同じです。
     * </p>
     *
     * @param heim POPインスタンスが所属するHeim。
     * @param id POP IDまたはPOP IDとインスタンスIDを連結した文字列。
     * @return Popインスタンス。
     */
    Pop getPop(int heimId, String id);


    /**
     * 指定されたIDに対応するPopインスタンスを返します。
     * <p>IDとしては、POP ID、POP IDにインスタンスIDを「<code>-</code>」で結合したもの、
     * のいずれかを指定することができます。
     * </p>
     * <p>対応するPopインスタンスが存在しない場合は、
     * 指定されたPOP IDに対応するPOPエントリが存在しないのであればnullを返します。
     * POPエントリが存在する場合で、単にインスタンスIDに対応するインスタンスが存在しない場合、
     * <code>create</code>がtrueであればインスタンスを生成して返します。
     * </p>
     *
     * @param heim POPインスタンスが所属するHeim。
     * @param id POP IDまたはPOP IDとインスタンスIDを連結した文字列。
     * @param create POPインスタンスが存在しない場合に生成するかどうか。
     * @return Popインスタンス。
     */
    Pop getPop(int heimId, String id, boolean create);


    Pop[] getPops(int heimId);


    void removePop(int heimId, Object key, int instanceId);


    void removePop(Pop pop);


    Resource getDefaultPopImageResource();
}
