package org.seasar.kvasir.base.descriptor;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;


/**
 * <p><b>注意：</b>
 * getterメソッドが返すオブジェクトは変更しないで下さい。</p>
 * <p><b>同期化：</b>
 * このクラスが持つsetterメソッドは、
 * フレームワークによって非同期に呼び出さなれないことが保証されています。
 * getterメソッドは非同期に呼び出されることがあります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ExtensionElement
{
    String ID_FIRST = "_first";

    String ID_LAST = "_last";


    Plugin<?> getPlugin();


    Extension getParent();


    String getId();


    String getFullId();


    String getAction();


    ActionType getActionType();


    String getBefore();


    String getAfter();


    Object getComponent();


    /*
     * for framework
     */

    void setParent(Extension parent);


    @Attribute
    void setId(String id);


    @Attribute
    void setAction(String action);


    @Attribute
    @Default(ID_LAST)
    void setBefore(String before);


    @Attribute
    @Default(ID_FIRST)
    void setAfter(String after);
}
