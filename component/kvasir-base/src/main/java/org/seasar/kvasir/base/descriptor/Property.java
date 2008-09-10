package org.seasar.kvasir.base.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Content;
import net.skirnir.xom.annotation.Parent;

/**
 * <p><b>注意：</b>
 * getterメソッドが返すオブジェクトは変更しないで下さい。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class Property
{
    private Object parent_;

    private String name_;

    private String value_;


    /*
     * public scope methods
     */

    public Object getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Object parent)
    {
        parent_ = parent;
    }


    public String getName()
    {
        return name_;
    }


    @Attribute
    public void setName(String name)
    {
        name_ = name;
    }


    public String getValue()
    {
        return value_;
    }


    @Content
    public void setValue(String value)
    {
        value_ = value.trim();
    }
}
