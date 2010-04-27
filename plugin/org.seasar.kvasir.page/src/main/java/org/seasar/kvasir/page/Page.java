package org.seasar.kvasir.page;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.type.User;


/**
 * Heim内のページを表すインタフェースです。
 *
 * @author YOKOTA Takehiko
 * @see PageAlfr
 */
public interface Page
    extends PageInternal
{
    String TYPE = "page";

    String FIELD_ID = "id";

    String FIELD_TYPE = "type";

    String FIELD_HEIMID = "heimid";

    String FIELD_LORD = "lord";

    String FIELD_PARENTPATHNAME = "parentPathname";

    String FIELD_NAME = "name";

    String FIELD_ORDERNUMBER = "orderNumber";

    String FIELD_CREATEDATE = "createDate";

    String FIELD_MODIFYDATE = "modifyDate";

    String FIELD_REVEALDATE = "revealDate";

    String FIELD_CONCEALDATE = "concealDate";

    String FIELD_OWNERUSER = "ownerUser";

    String FIELD_NODE = "node";

    String FIELD_ASFILE = "asFile";

    String FIELD_LISTING = "listing";

    int NULL_VALUE = -1;

    int ID_NONE = 0;

    int ID_ROOT = 1;

    int ID_ADMINISTRATOR_USER = 2;

    int ID_ANONYMOUS_USER = 3;

    int ID_ADMINISTRATOR_GROUP = 4;

    int ID_ALL_GROUP = 5;

    int ID_ADMINISTRATOR_ROLE = 6;

    int ID_ANONYMOUS_ROLE = 7;

    int ID_ALL_ROLE = 8;

    int ID_OWNER_ROLE = 9;

    int ID_ALFHEIM_ROOT = 10;

    int ID_USERS = 101;

    int ID_GROUPS = 102;

    int ID_ROLES = 103;

    int ID_TEMPLATES = 104;

    int ID_SYSTEM = 105;

    int ID_PLUGINS = 106;

    String PATHNAME_ROOT = "";

    String PATHNAME_USERS = "/users";

    String PATHNAME_GROUPS = "/groups";

    String PATHNAME_ROLES = "/roles";

    String PATHNAME_TEMPLATES = "/templates";

    String PATHNAME_SYSTEM = "/system";

    String PATHNAME_PLUGINS = "/plugins";

    String PATHNAME_ADMINISTRATOR_USER = PATHNAME_USERS + "/administrator";

    String PATHNAME_ANONYMOUS_USER = PATHNAME_USERS + "/anonymous";

    String PATHNAME_ADMINISTRATOR_GROUP = PATHNAME_GROUPS + "/administrator";

    String PATHNAME_ALL_GROUP = PATHNAME_GROUPS + "/all";

    String PATHNAME_ADMINISTRATOR_ROLE = PATHNAME_ROLES + "/administrator";

    String PATHNAME_ANONYMOUS_ROLE = PATHNAME_ROLES + "/anonymous";

    String PATHNAME_ALL_ROLE = PATHNAME_ROLES + "/all";

    String PATHNAME_OWNER_ROLE = PATHNAME_ROLES + "/owner";

    String VARIANT_DEFAULT = "";

    char SYMBOL_LORD_CHAR = '@';

    String SYMBOL_LORD = String.valueOf(SYMBOL_LORD_CHAR);

    String PROTOCOL_PAGE = "page:";

    String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    /** ページツリーのルートに対応する仮想的なGardのIDです。 */
    String GARDID_ROOT = "root";

    /** 世界が終わる日時です。 */
    Date DATE_RAGNAROK = new Object() {
        public Date execute()
        {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse("3000-01-01 00:00:00");
            } catch (ParseException ex) {
                throw new RuntimeException("Can't initialize DATE_RAGNAROK", ex);
            }
        }
    }.execute();


    /**
     * このページのIDを返します。
     * <p>このIDは全Heimを通して一意です。
     * 
     * @return ID。
     */
    int getId();


    /**
     * このページのパス名を返します。
     * <p>パス名はHeim内で一意な文字列識別子です。
     * </p>
     * 
     * @return パス名。
     */
    String getPathname();


    /**
     * このページのタイプを返します。
     * <p>タイプは通常{@link #TYPE}ですが、Pageタイプが拡張されている環境では他のタイプであることがあります。
     * org.seasar.kvasir.plugin.pageプラグインでは、次のタイプが存在します。
     * </p>
     * <ul>
     *   <li>directory</li>
     *   <li>group</li>
     *   <li>role</li>
     *   <li>user</li>
     * </ul>
     * 
     * @return タイプ。
     */
    String getType();


    /**
     * このページが指定されたページタイプかどうかを返します。
     * <p>nullを指定した場合はfalseが返されます。
     * </p>
     * 
     * @param type タイプ。
     * @return 指定されたページタイプかどうか。
     */
    boolean isTypeOf(String type);


    /**
     * このページのタイプを設定します。
     * <p>シリアライズされたPage情報などからPageオブジェクトを復元する場合など、
     * 特殊な用途でのみ利用されます。
     * Page機構に精通していないアプリケーション開発者がこのメソッドは使用するのは危険です。
     * </p>
     * 
     * @param type タイプ。
     */
    void setType(String type);


    /**
     * このページのLordのパス名を返します。
     * 
     * @return Lordのパス名。
     * @see #getLord()
     */
    String getLordPathname();


    /**
     * このページのLord相対のパス名を返します。
     * <p>このページのパス名が"/a/b/c"、Lordのパス名が"/a/b"である場合、
     * このメソッドの返り値は"/c"になります。
     * </p>
     * 
     * @return Lord相対のパス名。
     * @see #getLord()
     */
    String getLocalPathname();


    /**
     * このページのLordを返します。
     * <p>Lordとは、意味のある集まりをなすサブページツリーのルートであるページのことです。
     * Lordを見つけるルールは以下の通りです。
     * </p>
     * <ol>
     *   <li>このページ自身がLordであるなら、このページ自身。</li>
     *   <li>そうでなければ、このページの祖先Pageのうち一番近いLord。</li>
     * </ol>
     * また、各HeimのルートページはLordです。
     * </p>
     * 
     * @return Lord。
     */
    Page getLord();


    /**
     * このページまたはこのページの上位に存在する全てのLordの配列を返します。
     * <p>Lordの配列はルートに近い順で並んでいます。
     * 例えばこのページが"/a/b/c"で、"/a"がLordである場合は
     * { "", "/a" }という配列を返します。
     * </p>
     * 
     * @return このページまたはこのページの上位に存在する全てのLordの配列。
     */
    Page[] getLords();


    /**
     * このページの親のパス名を返します。
     * <p>このページがルートページの場合はnullを返します。
     * </p>
     * 
     * @return 親のパス名。
     */
    String getParentPathname();


    /**
     * このページの親Pageを返します。
     * <p>このページがルートページの場合はnullを返します。
     * </p>
     * 
     * @return 親Page。
     */
    Page getParent();


    /**
     * このページが属するHeimのルートページを返します。
     * 
     * @return このページが属するHeimのルートページ。
     */
    Page getRoot();


    /**
     * このページの名前を返します。
     * <p>名前は同一の親Pageの子について一意な文字列識別子です。
     * </p>
     * 
     * @return 名前。
     */
    String getName();


    /**
     * このページの順序番号を返します。
     * <p>順序番号は、同じ親の子Pageを並べる時に使用される番号です。
     * 通常は順序番号の順に並べられます。
     * </p>
     * <p>番号に一意性はありません。
     * 同一の順序番号を持つ2つの兄弟Pageがあった場合、
     * 並ぶ順序は不定です。
     * </p>
     * 
     * @return 順序番号。
     */
    int getOrderNumber();


    /**
     * このページの順序番号を設定します。
     * 
     * @param orderNumber 順序番号。
     * @see #getOrderNumber()
     */
    void setOrderNumber(int orderNumber);


    /**
     * このページの作成日時を返します。
     * 
     * @return 作成日時。
     */
    Date getCreateDate();


    /**
     * このページの作成日時を設定します。
     * 
     * @param createDate 作成日時。
     */
    void setCreateDate(Date createDate);


    /**
     * このページの作成日時を文字列形式で返します。
     * <p>日時は{@link #DATEFORMAT}に従ってフォーマットされます。
     * </p>
     * 
     * @return 作成日時の文字列形式。
     */
    String getCreateDateString();


    /**
     * このページの変更日時を返します。
     * 
     * @return 変更日時。
     */
    Date getModifyDate();


    /**
     * このページの変更日時を設定します。
     * 
     * @param modifyDate 変更日時。
     */
    void setModifyDate(Date modifyDate);


    /**
     * このページの変更日時を文字列形式で返します。
     * <p>日時は{@link #DATEFORMAT}に従ってフォーマットされます。
     * </p>
     * 
     * @return 変更日時の文字列形式。
     */
    String getModifyDateString();


    /**
     * このページが可視状態になる日時を返します。
     * 
     * @return 可視状態になる日時。
     * @see #setRevealDate(Date)
     */
    Date getRevealDate();


    /**
     * このページが可視状態になる日時を設定します。
     * <p>可視状態／不可視状態の制御は、「Pageを登録はするが公開は後で」ということを実現したい場合に行ないます。
     * Pageは{@link #getRevealDate()}になるまでは不可視状態、{@link #getRevealDate()}になると可視状態、
     * {@link #getConcealDate()}になると不可視状態になります。
     * </p>
     * <p>設定した日時になるまで、このページは不可視状態です。
     * 
     * @return 可視状態になる日時。
     * @see #setConcealDate(Date)
     */
    void setRevealDate(Date revealDate);


    /**
     * このページが可視状態になる日時を文字列形式で返します。
     * <p>日時は{@link #DATEFORMAT}に従ってフォーマットされます。
     * </p>
     * 
     * @return 可視状態になる日時の文字列形式。
     */
    String getRevealDateString();


    /**
     * このページが不可視状態になる日時を返します。
     * 
     * @return 不可視状態になる日時。
     * @see #setConcealDate(Date)
     */
    Date getConcealDate();


    /**
     * このページが不可視状態になる日時を設定します。
     * <p>可視状態／不可視状態の制御は、「ある日時になったらPageの公開を終了する」ということを実現したい場合に行ないます。
     * Pageは{@link #getRevealDate()}になるまでは不可視状態、{@link #getRevealDate()}になると可視状態、
     * {@link #getConcealDate()}になると不可視状態になります。
     * </p>
     * <p>設定した日時になると、このページは不可視状態になります。
     * 
     * @return 不可視状態になる日時。
     * @see #setRevealDate(Date)
     */
    void setConcealDate(Date concealDate);


    /**
     * このページが不可視状態になる日時を文字列形式で返します。
     * <p>日時は{@link #DATEFORMAT}に従ってフォーマットされます。
     * </p>
     * 
     * @return 不可視状態になる日時の文字列形式。
     */
    String getConcealDateString();


    /**
     * このページの所有者ユーザを返します。
     * 
     * @return 所有者ユーザ。
     */
    User getOwnerUser();


    /**
     * このページの所有者ユーザを設定します。
     * 
     * @param ownerUser 所有者ユーザ。
     */
    void setOwnerUser(User ownerUser);


    /**
     * このページが現在不可視状態かを返します。
     * 
     * @return 現在不可視状態か。
     */
    boolean isConcealed();


    /**
     * 指定された日時の時点でこのページが不可視状態かを返します。
     * 
     * @param date 日時。
     * @return 指定された日時の時点で不可視状態か。
     */
    boolean isConcealedWhen(Date date);


    /**
     * このページがNodeであるかどうかを返します。
     * <p>自分自身とは独立した子Pageを持つPageをNodeと呼びます。
     * あらゆるPageは子Pageを持つことができますが、例えば元Pageがメール本文、
     * 子Pageが添付ファイル、というようなPageの使い方をする場合等、
     * 子Pageが元Pageの従属物である場合はそのPageはNodeではありません。
     * </p>
     * <p>ルートページはNodeです。</p>
     * 
     * @return Nodeであるかどうか。
     */
    boolean isNode();


    /**
     * このページがNodeであるかどうかを設定します。
     * 
     * @param node Nodeであるかどうか。
     * @see #isNode()
     */
    void setNode(boolean node);


    /**
     * このページを表示する際に生のファイルとして扱うかどうかを返します。
     * 
     * @return 表示する際に生のファイルとして扱うかどうか。
     * @see #setAsFile(boolean)
     */
    boolean isAsFile();


    /**
     * このページを表示する際に生のファイルとして扱うかどうかを設定します。
     * <p>trueが指定されると、このページを表示する際にテンプレート等を使って整形して表示するのではなく、
     * 内容をそのまま表示するようになります。
     * falseが指定されると、このページを表示する際にテンプレート等を使って整形して表示するようになります。
     * </p>
     * 
     * @param asFile 表示する際に生のファイルとして扱うかどうか。
     */
    void setAsFile(boolean asFile);


    /**
     * このページを一覧表示に含めるかを返します。
     * 
     * @return 一覧表示に含めるか。
     * @see #setListing(boolean)
     */
    boolean isListing();


    /**
     * このページを一覧表示に含めるかを設定します。
     * <p>trueが指定されると、このページの親Pageについて子Pageの一覧を表示する場合、
     * falseが指定されると、一覧に含まれないようになります。
     * </p>
     * 
     * @param listing 一覧表示に含めるか。
     */
    void setListing(boolean listing);


    /**
     * このページが所属するHeimのIDを返します。
     * <p>返す値は、デフォルトのページツリーを表す「Midgard」の識別子である{@link PathId#HEIM_MIDGARD}か、
     * システム用のページツリーを表す「Alfheim」の識別子である{@link PathId#HEIM_ALFHEIM}か、
     * ユーザ定義のHeimの識別子です。
     * </p>
     * 
     * @return 所属するHeimのID。
     */
    int getHeimId();


    /**
     * このページまたはこのページの祖先Pageのうち、一番近いNodeを返します。
     * 
     * @return このページまたはこのページの祖先Pageのうち、一番近いNode。
     */
    Page getNearestNode();


    /**
     * このページがルートページかどうかを返します。
     * 
     * @return ルートページかどうか。
     */
    boolean isRoot();


    /**
     * このページがLordかどうかを返します。
     * 
     * @return Lordかどうか。
     */
    boolean isLord();


    /**
     * このページがLordかどうかを設定します。
     * 
     * @param set Lordかどうか。
     */
    void setAsLord(boolean set);


    /**
     * このページを別のパスに移動します。
     * <p>例えばこのページを"/a/b/c"として移動したい場合は、
     * <code>moveTo("/a/b", "c")</code>のように指定します。
     * </p>
     * <p>移動先が自分自身の場合は何もしません。
     * </p>
     * <p>ページを移動すると、子孫ページも一緒に移動されます。
     * </p>
     * <p>ルートページは移動することができません。
     * また自分自身の子孫ページに移動することはできません。
     * また移動先にページが既に存在する場合は移動することはできません。
     * また違うHeimに移動することはできません。
     * </p>
     * 
     * @param toParent 移動先の親ページ。
     * @param toName 移動後の名前。
     * @throws DuplicatePageException 移動先のページが既に存在している場合。
     * @throws LoopDetectedException 自分自身の子孫ページに移動しようとした場合。
     */
    void moveTo(Page toParent, String toName)
        throws DuplicatePageException, LoopDetectedException;


    /**
     * このページオブジェクトの状態を変更したことをフレームワークに通知します。
     * <p>このメソッドは<code>touch(true)</code>と同じです。
     * </p>
     * @see #touch(boolean)
     */
    void touch();


    /**
     * このページオブジェクトの状態を変更したことをフレームワークに通知します。
     * <p>このメソッドは、
     * 意味的に一まとまりの変更操作が完了したことを
     * フレームワークに通知するために使用します。</p>
     * <p>このメソッドが呼び出されると、更新日時が書き換えられます。
     * またPageEvent.UPDATEイベントが発生します。</p>
     * <p>意味的に一まとまりの変更操作が行なわれたタイミングで
     * 何らかの処理を行ないたい場合（検索インデックスへの登録、
     * 変更のメール通知など）はPageEvent.UPDATEイベントを処理するような
     * PageListenerをpageListeners拡張ポイントに登録して下さい。
     * また、アプリケーション開発者は、
     * このページオブジェクトについて
     * 複数の変更操作からなる意味的に一まとまりの変更操作を行なう毎に、
     * このメソッドを呼び出すようにして下さい。</p>
     * 
     * @param updateModifyDate Pageの更新日時フィールドを更新するかどうか。
     */
    void touch(boolean updateModifyDate);


    /**
     * 指定された名前の子ページを返します。
     * <p>nameには「/」を含む文字列を指定することができます。
     * その場合、直接の子ページではなく子孫ページを検索して返します。
     * </p>
     * <p>nameに「/」で始まる文字列を指定した場合、
     * 先頭の「/」は無視されます。</p>
     * 
     * @param name 子ページの名前。nullを指定することはできません。
     * @return 子ページを表すPageオブジェクト。対応する子ページが存在しない場合はnullを返します。
     */
    Page getChild(String name);


    /**
     * 指定された名前の子ページを返します。
     * <p>nameには「/」を含む文字列を指定することができます。
     * その場合、直接の子ページではなく子孫ページを検索して返します。
     * </p>
     * <p>nameに「/」で始まる文字列を指定した場合、
     * 先頭の「/」は無視されます。</p>
     * <p>子ページが指定された型でない場合はnullを返します。
     * </p>
     * 
     * @param name 子ページの名前。nullを指定することはできません。
     * @return 子ページを表すPageオブジェクト。対応する子ページが存在しない場合はnullを返します。
     */
    <P extends Page> P getChild(Class<P> clazz, String name);


    /**
     * このページの全ての子ページを返します。
     * 
     * @return 子ページの配列。子ページが存在しない場合は空の配列を返します。
     */
    Page[] getChildren();


    /**
     * このページの子ページのうち、指定された条件を満たすものを返します。
     * 
     * @param cond 条件。
     * @return 子ページの配列。指定された条件を満たす子ページが存在しない場合は空の配列を返します。
     */
    Page[] getChildren(PageCondition cond);


    /**
     * このページの全ての子ページの名前を返します。
     * 
     * @return 子ページの名前配列。子ページが存在しない場合は空の配列を返します。
     */
    String[] getChildNames();


    /**
     * このページの子ページのうち、指定された条件を満たすものの名前を返します。
     * 
     * @param cond 条件。
     * @return 子ページの名前の配列。指定された条件を満たす子ページが存在しない場合は空の配列を返します。
     */
    String[] getChildNames(PageCondition cond);


    /**
     * このページの全ての子ページの個数を返します。
     * 
     * @return 子ページの個数。
     */
    int getChildrenCount();


    /**
     * このページの子ページのうち、指定された条件を満たすものの個数を返します。
     * 
     * @param cond 条件。
     * @return 子ページの個数。
     */
    int getChildrenCount(PageCondition cond);


    /**
     * 子ページを作成します。
     * 
     * @param mold 子ページの内容。
     * @return 作成した子ページ。
     * @throws DuplicatePageException 既に同じ名前の子ページが存在する場合。
     */
    Page createChild(PageMold mold)
        throws DuplicatePageException;


    /**
     * このページを削除します。
     * <p>子孫ページを持つ場合はそれらも削除されます。
     * </p>
     * <p>ページを削除した後はこのオブジェクトのメソッドを呼び出さないで下さい。
     * </p>
     */
    void delete();


    /**
     * このページに排他ロックをかけた状態で指定された処理を実行します。
     * 
     * @param <R> 処理結果の型。
     * @param processable 処理を表すオブジェクト。
     * @return 処理結果。
     */
    <R> R runWithLocking(Processable<R> processable);


    /**
     * 指定されたキーにマッチするAbilityオブジェクトを返します。
     * <p>Abilityとは、ページに関連付ける属性や操作を表す概念です。
     * <code>org.seasar.kvasir.page</code>プラグインでは、
     * <code>org.seasar.kvasir.page.pageAbilityAlfrs</code>拡張ポイントを
     * 拡張することでAbilityを追加することができます。
     * <code>org.seasar.kvasir.page</code>プラグインで定義されているAbilityとしては、
     * キーと値の組であるプロパティをページに持たせるためのPropertyAbility、
     * ユーザの集合を表す「グループ」を操作するためのAPIを提供するGroupAbility、
     * ユーザやグループに付与する操作権限を表す「ロール」を操作するためのAPIを提供するRoleAbility、
     * ページに対して操作権限を指定できるようにするためのPermissionAbilityがあります。
     * </p>
     * 
     * @param key Abilityを表すキー。通常はAbilityを表すインタフェースのclassオブジェクト、または
     * Abilityの短縮IDを指定します。
     * @return キーに対応するAbilityオブジェクト。見つからなかった場合はnullを返します。
     */
    Object getAbility(Object key);


    /**
     * 指定されたキーにマッチするAbilityを返します。
     * 
     * @param key Abilityを表すキー。通常はAbilityを表すインタフェースのclassオブジェクトを指定します。
     * @return キーに対応するAbilityオブジェクト。見つからなかった場合はnullを返します。
     * @see #getAbility(Object)
     */
    <P extends PageAbility> P getAbility(Class<P> key);


    /**
     * 指定された名前を元に、
     * このページのどの子ページの名前とも一致しないような
     * 名前文字列を返します。
     * <p><code>name</code>を持つ子ページが存在しない場合は
     * <code>name</code>をそのまま返します。
     * 存在する場合は<code>name</code>
     * の末尾に数字を付けた文字列のうち
     * どの子ページの名前とも一致しない文字列を返します。
     * </p>
     * <p>このメソッドを使って取得した文字列を名前として
     * 子ページを作成する場合は、
     * このメソッド呼び出しと子ページの作成処理を
     * アトミックに行なう必要があります。
     * そのため、通常はこのメソッド呼び出しは{@link #exclusiveLock(boolean)}
     * によるロック区間内で行なわれます。
     * </p>
     *
     * @param name 名前文字列。
     * @return どの子ページの名前とも一致しない名前文字列。
     */
    String getNonExistentChildName(String name);


    /**
     * このページに指定されているGardのIDがあれば返します。
     * <p>このページがGardのルートである場合はそのGardのIDを返します。
     * そうでない場合はnullを返します。</p>
     *
     * @return このページに指定されているGardのID。
     * @see PageGard
     */
    String getGardId();


    /**
     * このページが属するGardのIDの配列を返します。
     * <p>ルートページに近い順に返します。</p>
     * <p>Gardであったとしてもルートページは含みません。</p>
     *
     * @return GardのIDの配列。
     * @see PageGard
     */
    String[] getGardIds();


    /**
     * このページが属するGardに関するGardルートページの配列を返します。
     * <p>ルートページに近い順に返します。</p>
     * <p>ルートページがGardであるかどうかに関わらず、最初の要素として必ずルートページが返ります。</p>
     *
     * @return Gardルートページの配列。
     * @see PageGard
     */
    Page[] getGardRoots();


    /**
     * このページに指定されているGardのバージョンがあれば返します。
     * <p>このページがGardのルートである場合はそのGardのバージョンを返します。
     * そうでない場合はnullを返します。</p>
     *
     * @return このページに指定されているGardのバージョン。
     * @see PageGard
     */
    Version getGardVersion();


    /**
     * このページが属するGardのうち、最も近いGardに関するGardルートページを返します。
     * <p>どのGardにも属さない場合や、最も近いGardルートページがルートページである場合は
     * ルートページを返します。
     * </p>
     *
     * @return Gardルートページの配列。
     * @see PageGard
     */
    Page getNearestGardRoot();


    /**
     * このページのフィールドを更新する際に楽観的ロックを行なうかを返します。
     * 
     * @return フィールドを更新する際に楽観的ロックを行なうか。
     */
    boolean isOptimisticLockEnabled();


    /**
     * このページのフィールドを更新する際に楽観的ロックを行なうかを設定します。
     * <p>trueを設定すると、このページのフィールドを更新する際に楽観的ロックを行なうようになります。
     * フィールドを更新しようとした際にページの変更が検出された場合、
     * {@link CollisionDetectedRuntimeException}がスローされます。
     * </p>
     * 
     * @return フィールドを更新する際に楽観的ロックを行なうか。
     */
    boolean setOptimisticLockEnabled(boolean optimisticLockEnabled);


    /**
     * このオブジェクトの内容を最新の状態に更新します。
     * <p>ページの内容は他のスレッド等によって変更されることがありますが、
     * 変更が随時Pageオブジェクトに反映されるわけではありません。
     * このメソッドを呼び出すことによって、このオブジェクトの内容が最新のページの状態に更新されます。
     * </p>
     */
    void refresh();


    /**
     * Pageオブジェクトを管理するPageAlfrを返します。
     * 
     * @return PageAlfr。
     */
    PageAlfr getAlfr();
}
