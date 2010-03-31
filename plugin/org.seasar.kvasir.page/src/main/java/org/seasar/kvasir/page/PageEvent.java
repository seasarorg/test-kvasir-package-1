package org.seasar.kvasir.page;

/**
 * Pageの状態変化に関するイベントを表すクラスです。
 * 
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class PageEvent
{
    /** Pageが作成されたことを表します。 */
    public static final int CREATED = 0;

    /** Pageが移動されたことを表します。 */
    public static final int MOVED = 1;

    /** Pageが非表示状態になったことを表します。 */
    public static final int CONCEALED = 2;

    /** Pageが削除されたことを表します。 */
    public static final int DELETED = 3;

    /** Pageが別のLoadに属するようになったことを表します。 */
    public static final int LORD_CHANGED = 4;

    /** Pageが持つなんらかの属性が更新されたことを表します。 */
    public static final int UPDATED = 5;

    private int type_;


    protected PageEvent(int type)
    {
        type_ = type;
    }


    /**
     * 発生したイベントの種類を返します。
     * 
     * @return 発生したイベントの種類。
     */
    public int getType()
    {
        return type_;
    }
}
