package org.seasar.kvasir.page;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class DeletedPageEvent extends PageEvent
{
    private int         id_;
    private String      pathname_;


    public DeletedPageEvent(int id, String pathname)
    {
        super(PageEvent.DELETED);

        id_ = id;
        pathname_ = pathname;
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }
}
