package org.seasar.kvasir.page;

/**
 * Pageが削除された場合に発生するPageEventです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class DeletedPageEvent extends PageEvent
{
    private int id_;

    private int heimId_;

    private String pathname_;


    public DeletedPageEvent(int id, int heimId, String pathname)
    {
        super(PageEvent.DELETED);

        id_ = id;
        heimId_ = heimId;
        pathname_ = pathname;
    }


    /**
     * 削除されたPageのIDを返します。
     * 
     * @return 削除されたPageのID。
     */
    public int getId()
    {
        return id_;
    }


    /**
     * 削除されたPageが属していたHeimのIDを返します。
     * 
     * @return 削除されたPageが属していたHeimのID。
     */
    public int getHeimId()
    {
        return heimId_;
    }


    /**
     * 削除されたPageのパス名を返します。
     * 
     * @return 削除されたPageのパス名。
     */
    public String getPathname()
    {
        return pathname_;
    }
}
