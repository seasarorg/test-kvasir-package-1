package org.seasar.kvasir.util.el.impl;

import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.el.VariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスがスレッドセーフであるかどうかは、
 * 内包するPropertyHandlerがスレッドセーフであるかどうかに依存します。
 * 通常はスレッドセーフであるようなPropertyHandlerをコンストラクタに渡すか、
 * 渡したPropertyHandlerの構造を外部から変更しないようにして下さい。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PropertyHandlerVariableResolver
    implements VariableResolver
{
    private PropertyHandler prop_;


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param prop PropertyHandlerオブジェクト。
     */
    public PropertyHandlerVariableResolver(PropertyHandler prop)
    {
        prop_ = prop;
    }


    /*
     * VariableResolver
     */

    public Object getValue(Object key)
    {
        if (key instanceof String) {
            return prop_.getProperty((String)key);
        } else {
            return null;
        }
    }
}
