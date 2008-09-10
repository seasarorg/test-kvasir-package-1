package org.seasar.kvasir.page.ability.table;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはス?レッドセーフである?必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TableAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "table";


    String[] getTableNames(Page page);
}
