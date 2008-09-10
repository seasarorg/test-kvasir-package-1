package org.seasar.kvasir.cms.manage.menu;

import java.util.Locale;

import org.seasar.kvasir.page.Page;


/**
 * 新規作成一覧に表示するエントリを表すクラスです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface NewItemMenuEntry
{
    /**
     * 「ページオブジェクト」カテゴリの識別子です。
     */
    String CATEGORY_PAGE = "page";

    /**
     * 「その他」カテゴリの識別子です。
     */
    String CATEGORY_ELSE = "else";


    /**
     * 指定されたページの子ページ一覧表示ビューの新規作成一覧に
     * エントリを表示するかどうかを返します。
     *
     * @param page ページ。
     * @return エントリを表示するかどうか。
     */
    boolean isDisplayed(Page page);


    /**
     * 新規作成一覧に表示する際のエントリの表示名を返します。
     *
     * @param locale ロケール。
     * @return 表示名。
     */
    String getDisplayName(Locale locale);


    /**
     * このエントリの識別子を返します。
     *
     * @return 識別子。
     */
    String getName();


    /**
     * エントリが選択された際の処理先のパスを返します。
     *
     * @return 処理先のパス。
     */
    String getPath();


    /**
     * エントリが選択された際の処理先のパスに追加する
     * リクエストパラメータを返します。
     *
     * @return リクエストパラメータ。
     * nullを返すことはありません。
     */
    String getParameter();


    /**
     * エントリが含まれるカテゴリの識別子を返します。
     *
     * @return カテゴリの識別子。
     */
    String getCategory();
}
