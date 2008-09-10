package org.seasar.kvasir.page.ability;

import java.util.Iterator;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageAbility
{
    void delete();

    /**
     * このPageが持つバリアントの配列を返します。
     * 
     * @param page ページ。
     * @return バリアントの配列。
     * <code>page</code>がnullの場合は空の配列、
     * またはベースバリアントだけを持つ配列を返します。
     */
    String[] getVariants();

    /**
     * このPageが持つ属性の値を返します。
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
    Attribute getAttribute(String name, String variant);

    void setAttribute(String name, String variant, Attribute value);

    void removeAttribute(String name, String variant);

    void clearAttributes();

    boolean containsAttribute(String name, String variant);

    Iterator<String> attributeNames(String variant); 

    Iterator<String> attributeNames(String variant, AttributeFilter filter); 
}
