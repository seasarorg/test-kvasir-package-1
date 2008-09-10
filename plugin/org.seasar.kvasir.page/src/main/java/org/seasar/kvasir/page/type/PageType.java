package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageType
{
    String getId();


    String getIconResourcePath();


    Resource getIconResource();


    Resource getConcealedIconResource();


    Class<? extends Page> getInterface();


    Page wrapPage(Page page);


    PageMold newPageMold();


    String convertFieldToPropertyName(String field);


    boolean isNumericField(String field);


    void processAfterCreated(Page page, PageMold mold);


    void processBeforeDeleting(Page page);
}
