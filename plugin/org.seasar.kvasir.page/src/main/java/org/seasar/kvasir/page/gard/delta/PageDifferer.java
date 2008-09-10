package org.seasar.kvasir.page.gard.delta;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageDifferer
{
    PageDifference diff(Page toPage, Page fromPage);

    void apply(Page page, PageDifference pd);
}
