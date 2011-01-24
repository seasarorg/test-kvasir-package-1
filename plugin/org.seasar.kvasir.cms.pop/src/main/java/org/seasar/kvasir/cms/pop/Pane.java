package org.seasar.kvasir.cms.pop;

import java.util.Locale;


/**
 * Webページ内でPOPを配置できる領域である「ペイン」を表すインタフェースです。
 * 
 * @author yokota
 *
 */
public interface Pane
{
    /**
     * IDを返します。
     * 
     * @return ID。
     */
    String getId();


    /**
     * このペインの表示名を返します。
     * 
     * @param locale ロケール。
     * @return 表示名。指定されていない場合はnullを返します。
     */
    String getLabel(Locale locale);


    /**
     * このペインが持っているPOPの配列を返します。
     * 
     * @return POPの配列。
     * nullが返されることはありません。
     */
    Pop[] getPops();


    /**
     * このペインにPOPを追加します。
     * 
     * @param pop 追加したいPOP。
     * nullを指定してはいけません。
     */
    void addPop(Pop pop);


    /**
     * このペインのPOPを指定されたものと置き換えます。
     * 
     * @param pops POPの配列。
     * nullを指定してはいけません。
     */
    void setPops(Pop[] pops);
}
