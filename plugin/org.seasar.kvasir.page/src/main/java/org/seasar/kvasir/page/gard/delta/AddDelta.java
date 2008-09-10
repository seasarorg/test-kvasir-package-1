package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class AddDelta extends AbstractDelta
{
    public AddDelta(String name, Object to)
    {
        super(TYPE_ADD, name, to, null, null);
    }
}
