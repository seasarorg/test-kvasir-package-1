package org.seasar.kvasir.page.condition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.type.User;


/**
 * Pageを検索するための検索条件を表すクラスです。
 * <p>初期状態では全てのPageを表します。（ただし再帰検索は無し）
 * 必要に応じてSetterを使って条件を指定して利用します。
 * </p>
 * <p>このオブジェクトを複数スレッドで共有するには、条件を設定した後にfreeze()メソッドを呼び出して下さい。
 * なおfreeze()メソッドの呼び出し以降は複数スレッドで利用することができますが、検索条件の変更はできなくなります。
 * </p>
 * <p>検索結果の並び順を指定するには{@link #setOrder(Order)}または{@link #addOrder(Order)}メソッドを
 * 使います。これらのメソッドを呼び出さなかった場合はデフォルトの並び順としてPageのorderNumberフィールドの値の昇順に並べられます。
 * パフォーマンス向上等のためにデフォルトの並び順を指定したくない場合は<code>setOrder(null)</code>として下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスは{@link PageCondition#freeze()}
 * を呼び出した後はスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageCondition
    implements Cloneable
{
    public static final String FIELD_ID = Page.FIELD_ID;

    public static final String FIELD_TYPE = Page.FIELD_TYPE;

    public static final String FIELD_HEIMID = Page.FIELD_HEIMID;

    public static final String FIELD_PARENTPATHNAME = Page.FIELD_PARENTPATHNAME;

    public static final String FIELD_NAME = Page.FIELD_NAME;

    public static final String FIELD_ORDERNUMBER = Page.FIELD_ORDERNUMBER;

    public static final String FIELD_CREATEDATE = Page.FIELD_CREATEDATE;

    public static final String FIELD_MODIFYDATE = Page.FIELD_MODIFYDATE;

    public static final String FIELD_REVEALDATE = Page.FIELD_REVEALDATE;

    public static final String FIELD_CONCEALDATE = Page.FIELD_CONCEALDATE;

    public static final String FIELD_NODE = Page.FIELD_NODE;

    public static final String FIELD_ASFILE = Page.FIELD_ASFILE;

    public static final String FIELD_LISTING = Page.FIELD_LISTING;

    /*
     * ここにフィールドを増やした場合はTableConstants.COLS_PAGEにもエントリを増やすこと。
     */
    public static final String[] FIELDS = { FIELD_ID, FIELD_TYPE, FIELD_HEIMID,
        FIELD_PARENTPATHNAME, FIELD_NAME, FIELD_ORDERNUMBER, FIELD_CREATEDATE,
        FIELD_MODIFYDATE, FIELD_REVEALDATE, FIELD_CONCEALDATE, FIELD_NODE,
        FIELD_ASFILE, FIELD_LISTING, };

    public static final String ALIAS_PATHNAME = "pathname";

    /* アプリケーションからの利用のために定義している。アプリケーションからはaliasかfieldかを気にしたくないので。 */
    public static final String FIELD_PATHNAME = ALIAS_PATHNAME;

    public static final String[] ALIASES = { ALIAS_PATHNAME };

    public static final int OFFSET_FIRST = 0;

    public static final int LENGTH_ALL = -1;

    private int offset_ = OFFSET_FIRST;

    private int length_ = LENGTH_ALL;

    private List<Order> orderList_;

    private List<Formula> optionList_ = null;

    private boolean recursive_ = false;

    private String type_;

    private boolean includeConcealed_ = true;

    private boolean onlyListed_ = false;

    private Date currentDate_;

    private User user_ = null;

    private Privilege privilege_ = null;

    private boolean freeze_ = false;


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを構築します。
     */
    public PageCondition()
    {
        orderList_ = new ArrayList<Order>();
        orderList_.add(new Order(FIELD_ORDERNUMBER));
    }


    /*
     * public scope methods
     */

    @Override
    public Object clone()
    {
        try {
            PageCondition pc = (PageCondition)super.clone();
            if (orderList_ != null) {
                pc.orderList_ = new ArrayList<Order>();
                int size = orderList_.size();
                for (int i = 0; i < size; i++) {
                    pc.orderList_.add(orderList_.get(i));
                }
            }
            if (optionList_ != null) {
                pc.optionList_ = new ArrayList<Formula>();
                int size = optionList_.size();
                for (int i = 0; i < size; i++) {
                    pc.optionList_.add((Formula)optionList_.get(i).clone());
                }
            }
            pc.freeze_ = false;

            return pc;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 条件にマッチするPageの列の何番目からを検索結果として返すかを返します。
     * <p>この値が{@link #OFFSET_FIRST}である場合は最初のPageから返すようになります。
     * </p>
     * 
     * @return 条件にマッチするPageの列の何番目からを検索結果として返すか。
     */
    public int getOffset()
    {
        return offset_;
    }


    /**
     * 条件にマッチするPageの列のoffset番目から何個を検索結果として返すかを返します。
     * <p>この値が{@link #LENGTH_ALL}である場合は個数制限なしになります。
     * </p>
     * 
     * @return 条件にマッチするPageの列のoffset番目から何個を検索結果として返すか。
     */
    public int getLength()
    {
        return length_;
    }


    /**
     * 検索結果の並び順を表すOrderオブジェクトの配列を返します。
     * 
     * @return 検索結果の並び順を表すOrderオブジェクトの配列。nullを返すことはありません。
     */
    public Order[] getOrders()
    {
        if (orderList_ == null) {
            return new Order[0];
        } else {
            return orderList_.toArray(new Order[0]);
        }
    }


    /**
     * 詳細条件を返します。
     * <p>詳細条件が指定されていない場合はnullを返します。
     * </p>
     * 
     * @return 詳細条件を表すFormulaオブジェクト。
     */
    public Formula getOption()
    {
        if (optionList_ == null) {
            return null;
        } else {
            return Formula.intersection(optionList_.toArray(new Formula[0]));
        }
    }


    /**
     * 再帰的にPageを検索するかどうかを返します。
     * 
     * @return 再帰的にPageを検索するかどうか。
     */
    public boolean isRecursive()
    {
        return recursive_;
    }


    /**
     * 取得するPageのタイプを返します。
     * <p>Pageのタイプとは、{@link Page#getType()}で返される値です。
     * </p>
     * 
     * @return Pageのタイプ。
     */
    public String getType()
    {
        return type_;
    }


    /**
     * 不可視状態のPageを検索結果に含めるかを返します。
     * 
     * @return 不可視状態のPageを検索結果に含めるか。
     */
    public boolean isIncludeConcealed()
    {
        return includeConcealed_;
    }


    /**
     * 「一覧に含める」フラグが立っているPageだけを検索結果に含めるかどうかを返します。
     * 
     * @return 「一覧に含める」フラグが立っているPageだけを検索結果に含めるかどうか。
     */
    public boolean isOnlyListed()
    {
        return onlyListed_;
    }


    /**
     * 検索を行なう日時を返します。
     * <p>無指定の場合はnullを返します。
     * </p>
     * 
     * @return 検索を行なう日時。
     */
    public Date getCurrentDate()
    {
        return currentDate_;
    }


    /**
     * 検索範囲であるHeimのIDを返します。
     * 
     * @return 検索範囲であるHeimのID。
     */
    public int getHeimId()
    {
        return PathId.HEIM_MIDGARD;
    }


    /**
     * ユーザを返します。
     * <p>アクセス権限に関する検索条件が無指定の場合はnullを返します。
     * </p>
     * 
     * @return ユーザ。
     * @see #setUser(User)
     */
    public User getUser()
    {
        return user_;
    }


    /**
     * 権限を返します。
     * <p>アクセス権限に関する検索条件が無指定の場合はnullを返します。
     * </p>
     * 
     * @return 権限。
     * @see #setPrivilege(Privilege)
     */
    public Privilege getPrivilege()
    {
        return privilege_;
    }


    /**
     * 条件にマッチするPageの列の何番目からを検索結果として返すかを設定します。
     * <p>{@link #OFFSET_FIRST}を設定した場合は最初のPageから返すようになります。
     * </p>
     * 
     * @param offset 条件にマッチするPageの列の何番目からを検索結果として返すか。
     * @return このオブジェクト自身。
     */
    public PageCondition setOffset(int offset)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        offset_ = offset;

        return this;
    }


    /**
     * 条件にマッチするPageの列のoffset番目から何個を検索結果として返すかを設定します。
     * <p>{@link #LENGTH_ALL}を設定した場合は個数制限なしになります。
     * </p>
     * 
     * @param length 条件にマッチするPageの列のoffset番目から何個を検索結果として返すか。
     * @return このオブジェクト自身。
     */
    public PageCondition setLength(int length)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        length_ = length;

        return this;
    }


    /**
     * 検索結果の並び順に関する条件を設定します。
     * <p>現在の設定は洗い換えされます。
     * </p>
     * <p>nullでない値を指定した場合は検索結果がその並び順で並べられて返されるようになります。
     * nullを指定した場合はデフォルトの並び順もクリアされ、
     * 並び順が指定されていない状態で結果が返されるようになります。
     * </p>
     * <p>並び順に関する条件を複数指定したい場合は{@link #setOrders(Order[])}
     * または{@link #addOrder(Order)}を使用して下さい。
     * </p>
     * 
     * @param order 検索結果の並び順を表すOrderオブジェクト。nullを指定することもできます。
     * @return このオブジェクト自身。
     * @see #addOrder(Order)
     */
    public PageCondition setOrder(Order order)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if (order == null) {
            orderList_ = null;
        } else {
            if (orderList_ == null) {
                orderList_ = new ArrayList<Order>();
            }
            orderList_.add(order);
        }

        return this;
    }


    /**
     * 検索結果の並び順を表すOrderオブジェクトの配列を設定します。
     * <p>現在の設定は洗い換えされます。
     * </p>
     * <p>nullを指定した場合はデフォルトの並び順もクリアされ、
     * 並び順が指定されていない状態で結果が返されるようになります。
     * </p>
     * 
     * @param orders 検索結果の並び順を表すOrderオブジェクトの配列。nullを指定することもできます。
     * @return このオブジェクト自身。
     * @see #setOrder(Order)
     */
    public PageCondition setOrders(Order[] orders)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if ((orders == null) || (orders.length == 0)) {
            orderList_ = null;
        } else {
            if (orderList_ == null) {
                orderList_ = new ArrayList<Order>(orders.length);
                for (int i = 0; i < orders.length; i++) {
                    orderList_.add(orders[i]);
                }
            }
        }

        return this;
    }


    /**
     * 検索結果の並び順を設定します。
     * <p>現在の設定に追加で設定されます。
     * </p>
     * <p>並び順に関する条件を置き換えたい場合は{@link #setOrders(Order[])}
     * または{@link #setOrder(Order)}を使用して下さい。
     * </p>
     * 
     * @param order 検索結果の並び順を表すOrderオブジェクト。nullを指定することもできます。
     * @return このオブジェクト自身。
     */
    public PageCondition addOrder(Order order)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if (order == null) {
            return this;
        }

        if (orderList_ == null) {
            orderList_ = new ArrayList<Order>();
        } else {
            int size = orderList_.size();
            for (int i = 0; i < size; i++) {
                Order ord = orderList_.get(i);
                if (ord.equals(order)) {
                    return this;
                }
            }
        }
        orderList_.add(order);

        return this;
    }


    /**
     * 詳細条件を設定します。
     * <p>現在の設定は洗い換えされます。
     * </p>
     * <p>詳細条件を複数指定したい場合は{@link #setOptions(Formula[])}
     * または{@link #addOption(Formula)}を使用して下さい。
     * </p>
     * 
     * @param option 詳細条件を表すFormulaオブジェクト。nullを指定することもできます。
     * @return このオブジェクト自身。
     */
    public PageCondition setOption(Formula option)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if (option == null) {
            optionList_ = null;
        } else {
            if (optionList_ == null) {
                optionList_ = new ArrayList<Formula>();
            } else {
                optionList_.clear();
            }
            optionList_.add(option);
        }

        return this;
    }


    /**
     * 詳細条件を設定します。
     * <p>現在の設定に追加で設定されます。
     * </p>
     * <p>詳細条件を置き換えたい場合は{@link #setOptions(Formula[])}
     * または{@link #setOption(Formula)}を使用して下さい。
     * </p>
     * 
     * @param option 詳細条件を表すFormulaオブジェクト。nullを指定することもできます。
     * @return このオブジェクト自身。
     */
    public PageCondition addOption(Formula option)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if (option == null) {
            return this;
        }
        if (optionList_ == null) {
            optionList_ = new ArrayList<Formula>();
        }
        optionList_.add(option);

        return this;
    }


    /**
     * 再帰的にPageを検索するかどうかを設定します。
     * <p>未設定の場合は再帰的に検索されません。</p>
     * 
     * @param recursive 再帰的にPageを検索するかどうか。
     * @return このオブジェクト自身。
     */
    public PageCondition setRecursive(boolean recursive)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        recursive_ = recursive;

        return this;
    }


    /**
     * 取得するPageのタイプを設定します。
     * <p>Pageのタイプとは、{@link Page#getType()}で返される値です。
     * </p>
     * <p>条件をクリアしたい場合はnullを設定して下さい。
     * </p>
     * 
     * @param type Pageのタイプ。
     * @return このオブジェクト自身。
     */
    public PageCondition setType(String type)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        type_ = type;

        return this;
    }


    /**
     * 不可視状態のPageを検索結果に含めるかを設定します。
     * <p>trueを設定すると、{@link Page#isConcealed()}がtrueであるPageも検索結果に含まれるようになります。
     * falseを設定すると、{@link Page#isConcealed()}がfalseであるPageだけが検索結果に含まれるようになります。
     * </p>
     * 
     * @param includeConcealed 不可視状態のPageを検索結果に含めるか。
     * @return このオブジェクト自身。
     */
    public PageCondition setIncludeConcealed(boolean includeConcealed)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        includeConcealed_ = includeConcealed;

        return this;
    }


    /**
     * 「一覧に含める」フラグが立っているPageだけを検索結果に含めるかどうかを指定します。
     * <p>trueを指定した場合、「一覧に含める」フラグが立っているPageだけが検索結果に含まれるようになります。
     * </p>
     * 
     * @param onlyListed 「一覧に含める」フラグが立っているPageだけを検索結果に含めるかどうか。
     * @return このオブジェクト。
     */
    public PageCondition setOnlyListed(boolean onlyListed)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        onlyListed_ = onlyListed;

        return this;
    }


    /**
     * 検索を行なう日時を設定します。
     * <p>この日時は、Pageが不可視状態かどうかをチェックする際に利用されます。
     * ある日時の時点で可視状態だったPageだけを取得したい場合などは、
     * このメソッドで日時を指定するようにして下さい。
     * </p>
     * <p>指定しなかった場合は現在の日時が利用されます。
     * </p>
     * <p>条件をクリアしたい場合はnullを設定して下さい。
     * </p>
     * 
     * @param currentDate 検索を行なう日時。
     * @return このオブジェクト。
     */
    public PageCondition setCurrentDate(Date currentDate)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        currentDate_ = currentDate;

        return this;
    }


    /**
     * アクセス権限に関する検索条件のうち、ユーザ条件を設定します。
     * <p>「ユーザAが閲覧権限を持つPage」のような
     * アクセス権限に関する検索条件を指定する場合、ユーザと権限を指定することになりますが、
     * このメソッドはアクセス権限に関する検索条件のうちのユーザを設定します。
     * </p>
     * <p>ユーザと権限は両方nullか、または両方非nullである必要があります。
     * </p>
     * 
     * @param user ユーザ。
     * @return このオブジェクト。
     * @see #setPrivilege(Privilege)
     */
    public PageCondition setUser(User user)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        user_ = user;

        return this;
    }


    /**
     * アクセス権限に関する検索条件のうち、権限条件を設定します。
     * <p>「ユーザAが閲覧権限を持つPage」のような
     * アクセス権限に関する検索条件を指定する場合、ユーザと権限を指定することになりますが、
     * このメソッドはアクセス権限に関する検索条件のうちの権限を設定します。
     * </p>
     * <p>ユーザと権限は両方nullか、または両方非nullである必要があります。
     * </p>
     * 
     * @param privilege 権限を表すPrivilegeオブジェクト。
     * @return このオブジェクト。
     * @see #setUser(User)
     */
    public PageCondition setPrivilege(Privilege privilege)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        privilege_ = privilege;

        return this;
    }


    /**
     * このオブジェクトを複数スレッドから利用できるよう、以降変更が行なえないようにします。
     * <p>このメソッドを呼び出した後は複数スレッドから安全に利用できるようになりますが、
     * 内容の変更はできなくなります。
     * </p>
     * 
     * @return このオブジェクト。
     */
    public PageCondition freeze()
    {
        freeze_ = true;
        if (optionList_ != null) {
            int size = optionList_.size();
            for (int i = 0; i < size; i++) {
                optionList_.get(i).freeze();
            }
        }

        return this;
    }


    /**
     * 変更が行なえない状態になっているかどうかを返します。
     * 
     * @return 変更が行なえない状態になっているかどうか。
     * @see #freeze()
     */
    public boolean isFreezed()
    {
        return freeze_;
    }
}
