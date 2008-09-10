package org.seasar.kvasir.base.dao.extension;


/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * DatabaseSystem登録のための拡張ポイントのIDです。
     */
    String DATABASESYSTEMS = "databaseSystems";


    /**
     * 対応するテーブルを自動的に生成したり管理したりする
     * S2Dao用JavaBeanを登録するための拡張ポイントのIDです。
     */
    String PERSISTENTBEANS = "persistentBeans";
}
