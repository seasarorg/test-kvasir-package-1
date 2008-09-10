package org.seasar.kvasir.page;

/**
 * ページのLordが変更された場合に発生するイベントを表すクラスです。
 * <p>ページのLordが変更された場合にこのクラスのインスタンスが生成され、
 * イベントリスナに対してイベントが通知される際に渡されます。
 * </p>
 * <p>ページのLordの変更イベントがイベントリスナに通知された時点では、
 * Lordのパス名が再帰的に排他ロックされていることが保証されます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class LordChangedPageEvent extends PageEvent
{
    private int heimId_;

    private String pathname_;

    private int oldLordId_;

    private int newLordId_;


    public LordChangedPageEvent(int heimId, String pathname, int oldLordId,
        int newLordId)
    {
        super(PageEvent.LORD_CHANGED);

        heimId_ = heimId;
        pathname_ = pathname;
        oldLordId_ = oldLordId;
        newLordId_ = newLordId;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public int getOldLordId()
    {
        return oldLordId_;
    }


    public int getNewLordId()
    {
        return newLordId_;
    }
}
