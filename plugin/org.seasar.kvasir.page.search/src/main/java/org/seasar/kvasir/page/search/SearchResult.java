package org.seasar.kvasir.page.search;

import org.seasar.kvasir.page.Page;


/**
 * 検索結果の1エントリを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SearchResult extends Cloneable
{
    Object clone();

    Page getPage();

    String getTitle();

    String getURL();

    float getScore();

    long getSize();

    String getSummary();

    String getVariant();

    String getProperty(String key);
}
