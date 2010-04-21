package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.io.Resource;


/**
 * Pageの種別を表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageType
{
    /**
     * この種別のIDを返します。
     * 
     * @return 種別のID。
     */
    String getId();


    /**
     * アイコンリソースのパスを返します。
     * <p>パスはプラグインディレクトリ相対パスです。
     * </p>
     * 
     * @return アイコンリソースのパス。
     */
    String getIconResourcePath();


    /**
     * アイコンリソースを返します。
     * 
     * @return アイコンリソース。
     * nullを返すことはありませんが、存在しないリソースを表すことはあります。
     */
    Resource getIconResource();


    /**
     * 非表示状態を表すアイコンのリソースを返します。
     * 
     * @return 非表示状態を表すアイコンのリソース。
     * nullを返すことはありませんが、存在しないリソースを表すことはあります。
     */
    Resource getConcealedIconResource();


    /**
     * この種別を表す{@link Page}インタフェースのサブインタフェースを返します。
     * 
     * @return Pageインタフェースのサブインタフェース。
     */
    Class<? extends Page> getInterface();


    /**
     * 指定されたPageオブジェクトをこの種別を表すPageオブジェクトでラップします。
     * <p>このメソッドが返すオブジェクトは{@link #getInterface()}が返すインタフェースを実装しています。
     * </p>
     * 
     * @param page ラップするPageオブジェクト。
     * nullを指定してはいけません。
     * @return ラップされたオブジェクト。
     * nullを返すことはありません。
     */
    Page wrapPage(Page page);


    /**
     * この種類のPageを作成したり変更したりするための情報を表すPageMoldのインスタンスを
     * 新たに作成して返します。
     * 
     * @return PageMoldインスタンス。
     */
    PageMold newPageMold();


    /**
     * 種別固有のフィールド名をプロパティ名に変換します。
     * <p>種別固有のフィールドはプロパティとして永続化されます。
     * そのため種別固有のフィールドが{@link PageCondition}で指定された場合、
     * プロパティに変換する必要があります。
     * このメソッドは{@link PageCondition}のために種別固有のフィールド名をプロパティに変換します。
     * </p>
     * <p><strong>実装上の注意：</strong>
     * 検索条件に指定することのないフィールドについてはこのメソッドでプロパティ名を返す必要はありません。
     * </p>
     * 
     * @param field フィールド名。
     * nullを指定してはいけません。
     * @return プロパティ名。
     * 存在しないフィールド名に対してはnullを返します。
     */
    String convertFieldToPropertyName(String field);


    /**
     * 種別固有のフィールドが数値かどうかを返します。
     * <p><strong>実装上の注意：</strong>
     * {@link #convertFieldToPropertyName(String)}が非nullを返すような
     * フィールドについてはこのメソッドで数値かどうかを返すようにしておく必要があります。
     * </p>
     * 
     * @param field フィールド名。
     * nullを指定してはいけません。
     * @return 数値かどうか。
     */
    boolean isNumericField(String field);


    /**
     * Pageを作成した直後に呼ばれるメソッドです。
     * <p>この種類のPageを作成した後になんらかの処理を行ないたい場合は
     * このメソッドで処理を行なって下さい。
     * </p>
     * 
     * @param page 作成されたPageオブジェクト。
     * nullが渡されることはありません。
     * @param mold 作成された際に用いられたPageMold。
     * nullが渡されることはありません。
     */
    void processAfterCreated(Page page, PageMold mold);


    /**
     * Pageを削除する直前に呼ばれるメソッドです。
     * <p>この種類のPageを削除する前になんらかの処理を行ないたい場合は
     * このメソッドで処理を行なって下さい。
     * </p>
     * 
     * @param page 削除されるPageオブジェクト。
     * nullが渡されることはありません。
     */
    void processBeforeDeleting(Page page);
}
