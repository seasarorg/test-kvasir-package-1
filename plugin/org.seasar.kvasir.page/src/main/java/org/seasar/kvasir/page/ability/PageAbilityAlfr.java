package org.seasar.kvasir.page.ability;

import java.util.Iterator;

import org.seasar.kvasir.page.Page;


/**
 * ページの能力を表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageAbilityAlfr
{
    String SUBNAME_DEFAULT = "";


    PageAbility getAbility(Page page);


    Class<? extends PageAbility> getAbilityInterfaceClass();


    String getShortId();


    /**
     * 指定されたページに関する初期化処理を行ないます。
     * <p>このメソッドはページが生成された直後に
     * フレームワークによって呼び出され、
     * 生成されたページについてこのabilityAlfr
     * に関する初期化処理が行なわれます。
     * </p>
     * <p><b>注意：</b>
     * このメソッドは親ページを排他ロックした状態で呼び出して下さい。</p>
     * 
     * @param page 生成されたページ。
     */
    void create(Page page);


    /**
     * 削除されるページに関する削除処理を行ないます。
     * <p>このメソッドはページが削除される直前に
     * フレームワークによって呼び出され、
     * 削除されるページについてこのabilityAlfr
     * に関する削除処理が行なわれます。
     * </p>
     * <p><b>注意：</b>
     * このメソッドはページを排他ロックした状態で呼び出して下さい。</p>
     * 
     * @param page 削除されるページ。
     */
    void delete(Page page);


    /**
     * 指定されたPageが持つバリアントの配列を返します。
     * 
     * @param page ページ。
     * @return バリアントの配列。
     * <code>page</code>がnullの場合は空の配列、
     * またはベースバリアントだけを持つ配列を返します。
     */
    String[] getVariants(Page page);


    /**
     * 指定されたPageが持つ属性の値を返します。
     * <p>対応する値が存在しない場合はnullを返します。</p>
     * 
     * @param page ページ。
     * @param name 属性名。nullを指定してはいけません。
     * 属性名は長さ1以上の文字列である必要があります。
     * 大文字小文字は区別されません。
     * @param variant バリアント。
     * @return 属性値。
     * <code>page</code>がnullの場合はnullを返します。
     */
    Attribute getAttribute(Page page, String name, String variant);


    void setAttribute(Page page, String name, String variant, Attribute value);


    void removeAttribute(Page page, String name, String variant);


    void clearAttributes(Page page);


    boolean containsAttribute(Page page, String name, String variant);


    Iterator<String> attributeNames(Page page, String variant);


    Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter);
}
