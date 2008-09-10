package org.seasar.kvasir.page;

/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class PageEvent
{
    public static final int         CREATED         = 0;
    public static final int         MOVED           = 1;
    public static final int         CONCEALED       = 2;
    public static final int         DELETED         = 3;
    public static final int         LORD_CHANGED    = 4;
    public static final int         UPDATED         = 5;


    private int     type_;


    protected PageEvent(int type)
    {
        type_ = type;
    }


    public int getType()
    {
        return type_;
    }
}
