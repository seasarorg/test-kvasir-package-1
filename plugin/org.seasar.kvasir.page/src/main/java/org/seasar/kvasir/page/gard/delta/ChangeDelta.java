package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ChangeDelta extends AbstractDelta
{
    public ChangeDelta(String name, Object to, Object from)
    {
        super(TYPE_CHANGE, name, to, from, null);
    }


    public ChangeDelta(String name, Object to, Object from, Object difference)
    {
        super(TYPE_CHANGE, name, to, from, difference);
    }
}
