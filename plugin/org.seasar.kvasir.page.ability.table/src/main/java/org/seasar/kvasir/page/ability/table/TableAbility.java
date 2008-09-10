package org.seasar.kvasir.page.ability.table;

import org.seasar.kvasir.page.ability.PageAbility;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはス?レッドセーフである?必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TableAbility
    extends PageAbility
{
    String[] getTableNames();
}
