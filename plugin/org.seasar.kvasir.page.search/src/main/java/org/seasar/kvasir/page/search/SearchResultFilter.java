package org.seasar.kvasir.page.search;

/**
 * 検索結果の取捨選択をするためのインタフェースです。
 * 検索結果はSearchQueryに指定した条件に従って取捨選択されますが、
 * それに加えてなんらかの特別な取捨選択処理をしたい場合はこのインタフェースの実装クラスを作成して
 * {@link SearchQuery#setSearchResultFilter(SearchResultFilter)}を使って
 * SearchQueryにセットして下さい。
 * </p>
 * 
 * @author skirnir
 * @see SearchQuery
 */
public interface SearchResultFilter
{
    /**
     * 指定された検索結果が可視かどうかを返します。
     * <p>このメソッドがfalseを返した検索結果は捨てられます。
     * </p>
     * 
     * @param result 検索結果。
     * nullが渡されることはありません。
     * @return 可視かどうか。
     */
    boolean isVisible(SearchResult result);
}
