package org.seasar.kvasir.page.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.type.User;


/**
 * 検索クエリを表すクラスです。
 * <p>このクラスは検索のためのクエリを表します。
 * SearchQueryオブジェクトを生成するには通常コンストラクタを用います。
 * 生成した後に、
 * 各setterメソッドを用いて検索クエリオブジェクトに検索条件を設定して下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスは{@link #freeze()}を呼び出すまではスレッドセーフではありません。
 * {@link #freeze()}を呼び出した後はスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SearchQuery
{
    public static final int OFFSET_FIRST = 0;

    public static final int LENGTH_ALL = -1;

    /** クエリ文字列です。 */
    private String queryString_ = "";

    /** 検索結果の最大数です。 */
    private int maxLength_ = LENGTH_ALL;

    /** 検索オプションです。 */
    private String option_ = "";

    /** 検索結果に不可視のページを含めるかどうかです。 */
    private boolean includeConcealed_ = false;

    /** 検索を実行するユーザです。 */
    private User user_ = null;

    /** アクセス権限です。 */
    private Privilege privilege_ = Privilege.ACCESS_VIEW;

    /** HeimのIDです。 */
    private Integer heimId_;

    /** ページタイプを保持するSetです。 */
    private Set<String> pageTypeSet_ = new HashSet<String>();

    /** 検索範囲に含めるページの配列です。 */
    private Page[] topPages_ = new Page[0];

    private Integer[] topHeimIds_ = new Integer[0];

    private String[] topPathnames_ = new String[0];

    private String[] topPathnameWithSlashes_ = new String[0];

    /** 検索対象のロケールです。 */
    private Locale locale_;

    /** 検索結果を取捨選択するためのフィルタです。  */
    private SearchResultFilter searchResultFilter_;

    private boolean freezed_ = false;


    /*
     * constructors
     */

    public SearchQuery()
    {
    }


    /*
     * public scope methods
     */

    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        SearchQuery query = (SearchQuery)obj;
        if (!equals(query.queryString_, queryString_)) {
            return false;
        }
        if (query.maxLength_ != query.maxLength_) {
            return false;
        }
        if (!equals(query.option_, option_)) {
            return false;
        }
        if (query.includeConcealed_ != includeConcealed_) {
            return false;
        }
        if (!equals(query.user_, user_)) {
            return false;
        }
        if (!equals(query.privilege_, privilege_)) {
            return false;
        }
        if (!equals(query.heimId_, heimId_)) {
            return false;
        }
        int size = query.pageTypeSet_.size();
        if (size != pageTypeSet_.size()) {
            return false;
        }
        for (Iterator<String> i1 = query.pageTypeSet_.iterator(), i2 = pageTypeSet_
            .iterator(); i1.hasNext();) {
            if (!i1.next().equals(i2.next())) {
                return false;
            }
        }
        size = query.topPages_.length;
        if (size != topPages_.length) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!query.topPages_[i].equals(topPages_[i])) {
                return false;
            }
        }
        if (!equals(query.locale_, locale_)) {
            return false;
        }
        return true;
    }


    boolean equals(Object o1, Object o2)
    {
        if (o1 != null) {
            return o1.equals(o2);
        } else if (o2 != null) {
            return o2.equals(o1);
        } else {
            return true;
        }
    }


    public String toString()
    {
        return new StringBuffer().append("queryString=").append(queryString_)
            .append(", maxLength=").append(maxLength_).append(", queryOption=")
            .append(queryString_).append(", includeConcealed=").append(
                includeConcealed_).append(", user=").append(user_).append(
                ", privilege=").append(privilege_).append(", heim=").append(
                heimId_).append(", pageTypeSet=").append(pageTypeSet_).append(
                ", topPages=").append(Arrays.asList(topPages_)).append(
                ", locale=").append(locale_).toString();
    }


    /**
     * 以降このオブジェクトの内容が変更できないようにフリーズします。
     * <p>一度フリーズした後は、
     * オブジェクトの内容を修正するようなメソッド呼び出しを行なうと
     * IllegalStateExceptionがスローされるようになります。</p>
     * <p>このメソッドはフレームワークが用います。</p>
     */
    public SearchQuery freeze()
    {
        freezed_ = true;
        return this;
    }


    /**
     * このオブジェクトの内容がフリーズされているかを返します。
     *
     * @return フリーズされているかどうか。
     */
    public boolean isFreezed()
    {
        return freezed_;
    }


    /**
     * クエリ文字列を返します。
     *
     * @return クエリ文字列。
     */
    public String getQueryString()
    {
        return queryString_;
    }


    /**
     * クエリ文字列を設定します。
     *
     * @param queryString クエリ文字列。
     */
    public SearchQuery setQueryString(String queryString)
    {
        checkFreezed();
        queryString_ = queryString;
        return this;
    }


    /**
     * 検索結果に不可視のページを含めるかどうかを返します。
     *
     * @return 検索結果に不可視のページを含めるかどうか。
     */
    public boolean isIncludeConcealed()
    {
        return includeConcealed_;
    }


    /**
     * 検索結果に不可視のページを含めるかどうかを設定します。
     *
     * @param includeConcealed 検索結果に不可視のページを含めるかどうか。
     */
    public SearchQuery setIncludeConcealed(boolean includeConcealed)
    {
        checkFreezed();
        includeConcealed_ = includeConcealed;
        return this;
    }


    public User getUser()
    {
        return user_;
    }


    public SearchQuery setUser(User user)
    {
        checkFreezed();
        user_ = user;
        return this;
    }


    /**
     * 検索オプションを返します。
     *
     * @return 検索オプション。
     */
    public String getOption()
    {
        return option_;
    }


    /**
     * 検索オプションを設定します。
     * <p>検索オプションは検索文字列に対するオプション指定です。
     * 検索オプションとして何を指定できるかは検索システム毎に異なります。
     * </p>
     *
     * @param option 検索オプション。
     */
    public SearchQuery setOption(String option)
    {
        checkFreezed();
        option_ = option;
        return this;
    }


    /**
     * アクセス権限を返します。
     *
     * @return アクセス権限。
     */
    public Privilege getPrivilege()
    {
        return privilege_;
    }


    /**
     * アクセス権限を設定します。
     * <p>指定されたユーザが検索結果に含めるページに対して持っているべきアクセス権限を設定します。
     * 言い換えると、
     * 指定されたアクセス権限を指定ユーザが持たないようなページは検索結果から除外されます。
     * </p>
     * <p>デフォルト値は
     * {@link Privilege#ACCESS_VIEW}です。</p>
     *
     * @param priv アクセス権限。
     */
    public SearchQuery setPrivilege(Privilege privilege)
    {
        checkFreezed();
        privilege_ = privilege;
        return this;
    }


    /**
     * HeimのIDを返します。
     *
     * @return HeimのID。
     */
    public Integer getHeimId()
    {
        return heimId_;
    }


    /**
     * Heimを設定します。
     * <p>検索結果をあるHeimに属するPageに限定したい場合はこのメソッドでHeimを設定して下さい。
     * </p>
     * <p>{@link #addTopPage(Page)}でトップページを指定した場合はこのメソッドでの指定は無視されます。
     * </p>
     *
     * @param heimId HeimのID。
     * @return このオブジェクト自身。
     */
    public SearchQuery setHeimId(Integer heimId)
    {
        checkFreezed();
        heimId_ = heimId;
        return this;
    }


    /**
     * 指定されたページタイプが含まれるかどうかを返します。
     * <p>指定されたページタイプを持つページが
     * この検索クエリの結果に含まれ得る場合はtrueを返します。
     * 結果に含まれ得るかは{@link #addPageType(String)}
     * で指定されたかどうかで決まります。
     *
     * @return ページタイプが結果に含まれ得るかどうか。
     */
    public boolean containsPageType(String pageType)
    {
        if (pageTypeSet_.size() > 0) {
            return pageTypeSet_.contains(pageType);
        } else {
            return true;
        }
    }


    /**
     * ページタイプを追加します。
     * <p>検索結果に含めるページのページタイプを追加します。
     * 言い換えると、
     * このメソッドで追加されたページタイプのいずれでもないページは検索結果から除外されます。
     * ただし、何も追加しなければ全てのページタイプが検索対象となります。
     * </p>
     * <p>デフォルトでは全てのページタイプが検索対象となります。</p>
     *
     * @param pageType ページタイプ。
     */
    public SearchQuery addPageType(String pageType)
    {
        checkFreezed();

        pageTypeSet_.add(pageType);
        return this;
    }


    /**
     * 検索範囲を表すページツリーのトップページを返します。
     *
     * @return 検索範囲を表すページツリーのトップページの配列。
     */
    public Page[] getTopPages()
    {
        return topPages_;
    }


    /**
     * 検索範囲を表すページツリーのトップページのリストを空にします。
     * <p>空にすると、検索範囲を限定しないようになります。</p>
     */
    public SearchQuery clearTopPages()
    {
        checkFreezed();
        topPages_ = new Page[0];
        topHeimIds_ = new Integer[0];
        topPathnames_ = new String[0];
        topPathnameWithSlashes_ = new String[0];
        return this;
    }


    /**
     * 検索範囲を表すページツリーのトップページを設定します。
     * <p>検索範囲を絞り込みたい場合は、
     * このメソッドを使って検索範囲に含めたいサブページツリーのトップページを追加して下さい。
     * 追加したページとその子孫ページが検索対象になります。
     * それ以外のページは検索対象から除外されます。
     * </p>
     * <p>ページはいくつでも追加できます。</p>
     * このメソッドでページを追加しなければ、
     * または{@link #clearTopPages()}を呼び出せば、
     * 検索範囲が限定されません。
     * </p>
     * <p>デフォルトでは検索範囲を限定しません。</p>
     *
     * @param topPage 検索範囲に含めるサブページツリーのトップページ。
     */
    public SearchQuery addTopPage(Page topPage)
    {
        checkFreezed();
        topPages_ = ArrayUtils.add(topPages_, topPage);
        String pathname = topPage.getPathname();
        topHeimIds_ = ArrayUtils.add(topHeimIds_, topPage.getHeimId());
        topPathnames_ = ArrayUtils.add(topPathnames_, pathname);
        topPathnameWithSlashes_ = ArrayUtils.add(topPathnameWithSlashes_,
            pathname + "/");

        return this;
    }


    public Locale getLocale()
    {
        return locale_;
    }


    public SearchQuery setLocale(Locale locale)
    {
        checkFreezed();
        locale_ = locale;
        return this;
    }


    public SearchResultFilter getSearchResultFilter()
    {
        return searchResultFilter_;
    }


    public SearchQuery setSearchResultFilter(
        SearchResultFilter searchResultFilter)
    {
        checkFreezed();
        searchResultFilter_ = searchResultFilter;
        return this;
    }


    private void checkFreezed()
    {
        if (freezed_) {
            throw new IllegalStateException("Can't set to freezed object");
        }
    }


    public boolean containsInTopPages(Page page)
    {
        if (topPages_.length > 0) {
            int heimId = page.getHeimId();
            String pathname = page.getPathname();
            for (int i = 0; i < topPages_.length; i++) {
                if (heimId == topHeimIds_[i]
                    && (pathname.equals(topPathnames_[i]) || pathname
                        .startsWith(topPathnameWithSlashes_[i]))) {
                    return true;
                }
            }

            return false;
        } else {
            return heimId_ == null || heimId_.intValue() == page.getHeimId();
        }
    }
}
