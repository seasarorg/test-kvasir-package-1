package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class RemoveDelta extends AbstractDelta
{
    public RemoveDelta(String name, Object from)
    {
        super(TYPE_REMOVE, name, null, from, null);
    }
}
