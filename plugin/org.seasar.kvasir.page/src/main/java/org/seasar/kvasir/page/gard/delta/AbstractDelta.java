package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class AbstractDelta
    implements Delta
{
    private int     type_;
    private String  name_;
    private Object  to_;
    private Object  from_;
    private Object  difference_;


    protected AbstractDelta(int type, String name, Object to, Object from,
        Object difference)
    {
        type_ = type;
        name_ = name;
        to_ = to;
        from_ = from;
        difference_ = difference;
    }


    public int getType()
    {
        return type_;
    }


    public String getName()
    {
        return name_;
    }


    public Object getTo()
    {
        return to_;
    }


    public Object getFrom()
    {
        return from_;
    }


    public Object getDifference()
    {
        return difference_;
    }
}
