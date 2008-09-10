package org.seasar.kvasir.base.dao;

import org.seasar.cms.beantable.Beantable;
import org.seasar.cms.database.identity.Identity;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface DaoPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.base.dao";

    String ID_PATH = ID.replace('.', '/');

    String PROP_DB_PRODUCTID = "db.productId";


    Identity getIdentity();


    String getDatabaseProductId();


    Beantable<?> getBeantable(String tableName);


    <T> Beantable<T> newBeantable(Class<T> beanClass);
}
