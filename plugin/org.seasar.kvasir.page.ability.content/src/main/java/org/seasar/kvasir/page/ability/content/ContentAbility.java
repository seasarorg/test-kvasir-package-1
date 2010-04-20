package org.seasar.kvasir.page.ability.content;

import java.util.Date;
import java.util.Locale;

import org.seasar.kvasir.page.ability.PageAbility;


/**
 * ページのコンテンツを扱うためのAbilityを表すインタフェースです。
 * <p>ページにはバリアント毎にコンテントを持たせることができます。
 * またそれぞれのコンテントは1から始まるリビジョン番号によって履歴管理されています。
 * </p>
 * <p>コンテントのリビジョン番号はバリアント毎に割り振られていますので、
 * 同一のリビジョン番号であっても内容が同期しているとは限らないことに注意して下さい。
 * 例えば最新の日本語のコンテントのリビジョン番号が10で英語のコンテントが存在しないケースで
 * 英語のコンテントを追加すると、英語のコンテントのリビジョン番号は1になります。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ContentAbility
    extends PageAbility
{
    /** 最初のリビジョン番号です。 */
    int REVISIONNUMBER_FIRST = 1;


    /**
     * コンテントの更新日時を返します。
     * 
     * @return 更新日時。
     */
    Date getModifyDate();


    /**
     * 指定されたバリアントのコンテントの履歴のうち最も古いもののリビジョン番号を返します。
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @return 最も古いリビジョン番号。
     * コンテントが存在しない場合は0が返されます。
     */
    int getEarliestRevisionNumber(String variant);


    /**
     * 指定されたバリアントのコンテントの履歴のうち最も新しいもののリビジョン番号を返します。
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @return 最も新しいリビジョン番号。
     * コンテントが存在しない場合は0が返されます。
     */
    int getLatestRevisionNumber(String variant);


    /**
     * 指定されたバリアントのコンテントの履歴から最も新しいものを返します。
     * <p>コンテントが存在しない場合はnullを返します。
     * </p>
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @return 最も新しいコンテント。
     */
    Content getLatestContent(String variant);


    /**
     * 指定されたロケールに対応するコンテントの履歴から最も新しいものを返します。
     * <p>コンテントが存在しない場合はnullを返します。
     * </p>
     * 
     * @param locale ロケール。
     * nullを指定してはいけません。
     * @return 最も新しいコンテント。
     */
    Content getLatestContent(Locale locale);


    /**
     * 指定された日時の時点での、指定されたロケールに対応するコンテントを返します。
     * <p>指定された日時の時点でコンテントが表示状態である場合はコンテントを返します。
     * 非表示状態である場合はnullを返します。
     * </p>
     * 
     * @param locale ロケール。
     * nullを指定してはいけません。
     * @param date 日時。
     * @return コンテント。
     */
    Content getContent(Locale locale, Date date);


    /**
     * 指定されたバリアントのコンテントの履歴から指定されたリビジョン番号のものを返します。
     * <p>リビジョン番号に対応するコンテントが存在しない場合はnullを返します。
     * </p>
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @param revisionNumber リビジョン番号。
     * @return コンテント。
     */
    Content getContent(String variant, int revisionNumber);


    /**
     * 指定されたバリアントのコンテントを上書きします。
     * <p>最新のコンテントが上書きされます。
     * リビジョン番号は更新されません。</p>
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @param mold 上書き内容を持つContentMoldオブジェクト。
     * nullを指定してはいけません。
     */
    void setContent(String variant, ContentMold mold);


    /**
     * 指定されたバリアントのコンテントを更新します。
     * <p>リビジョン番号が増加します。
     * </p>
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @param mold 更新内容を持つContentMoldオブジェクト。
     * nullを指定してはいけません。
     */
    void updateContent(String variant, ContentMold mold);


    /**
     * 指定されたリビジョン番号以前のコンテンツを履歴から削除します。
     * <p>例えばリビジョン番号として10を指定すると、最初から9までのリビジョンが削除されます。
     * </p>
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     * @param revisionNumber リビジョン番号。
     */
    void removeContentsBefore(String variant, int revisionNumber);


    /**
     * 指定されたバリアントのコンテントを全て削除します。
     * 
     * @param variant バリアント。
     * nullを指定してはいけません。
     */
    void clearContents(String variant);


    /**
     * 全てのバリアントについてコンテントを全て削除します。
     */
    void clearAllContents();
}
