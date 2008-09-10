package org.seasar.kvasir.page.ability.table.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.table.TableAbility;
import org.seasar.kvasir.page.ability.table.TableAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このクラスはス?レッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TableAbilityImpl extends AbstractPageAbility
    implements TableAbility
{
    private TableAbilityAlfr alfr_;


    public TableAbilityImpl(TableAbilityAlfr alfr, Page page)
    {
        super(alfr, page);
        alfr_ = alfr;
    }


    public String[] getTableNames()
    {
        return alfr_.getTableNames(page_);
    }
}
