package org.seasar.kvasir.page.ability.table;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface TableAbilityPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.ability.table";

    String ID_PATH = ID.replace('.', '/');


    /**
     * 指定されたgardに関連付けられているテーブルの名前の配列を返します。
     * <p>返されるテーブル名は大文字に正規化されています。</p>
     * <p>関連付けられているテーブルが存在しない場合は空の配列を返します。
     * </p>
     *
     * @param gardId gardのID。
     * @return テーブル名の配列。
     */
    String[] getTableNames(String gardId);


    /**
     * 指定されたテーブルが持つカラムのうち、レコードが関連付けられているページのIDを格納するためのカラムの名前を返します。
     * <p>返されるカラム名は大文字に正規化されています。</p>
     * <p>ページのIDを格納するためのカラムが存在しない場合はnullを返します。
     * </p>
     *
     * @param tableName テーブル名。
     * @return カラム名。
     */
    String getPageIdColumnName(String tableName);


    /**
     * 指定されたテーブルが持つカラムのうち、ページIDを格納するカラムの名前の配列を返します。
     * <p>返されるカラム名は大文字に正規化されています。</p>
     * <p>{@link #getPageIdColumnName(String)}が返すカラムは含まれません。
     * </p>
     * <p>ページIDを格納するカラムが存在しない場合は空の配列を返します。
     * </p>
     *
     * @param tableName テーブル名。
     * @return カラム名の配列。
     */
    String[] getPageIdRefColumnNames(String tableName);


    /**
     * 指定されたテーブルが持つカラムのうち、レコードのIDを格納するカラムの名前の配列を返します。
     * <p>返されるカラム名は大文字に正規化されています。</p>
     * <p>レコードのIDを格納するカラムが存在しない場合は空の配列を返します。
     * </p>
     *
     * @param tableName テーブル名。
     * @return カラム名の配列。
     */
    String[] getIdColumnNames(String tableName);
}
