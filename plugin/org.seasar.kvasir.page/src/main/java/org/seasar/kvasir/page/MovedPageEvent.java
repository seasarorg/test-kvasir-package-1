package org.seasar.kvasir.page;

/**
 * ページが移動した場合に発生するイベントを表すクラスです。
 * <p>ページが移動した場合にこのクラスのインスタンスが生成され、
 * イベントリスナに対してイベントが通知される際に渡されます。
 * </p>
 * <p>ページの移動イベントがイベントリスナに通知された時点では、
 * 移動したページの移動前のパス名と移動先のパス名が共に再帰的に排他ロックされていることが保障されます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MovedPageEvent extends PageEvent
{
    private int heimId_;

    private String fromPathname_;

    private Page page_;


    public MovedPageEvent(int heimId, String fromPathname, Page page)
    {
        super(PageEvent.MOVED);

        heimId_ = heimId;
        fromPathname_ = fromPathname;
        page_ = page;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public String getFromPathname()
    {
        return fromPathname_;
    }


    public Page getPage()
    {
        return page_;
    }
}
