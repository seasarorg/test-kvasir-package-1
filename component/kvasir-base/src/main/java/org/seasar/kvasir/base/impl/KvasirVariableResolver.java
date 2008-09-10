package org.seasar.kvasir.base.impl;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.util.el.VariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class KvasirVariableResolver
    implements VariableResolver
{
    public KvasirVariableResolver()
    {
    }


    /*
     * VariableResolver
     */

    public Object getValue(Object key)
    {
        if (key instanceof String) {
            return Asgard.getKvasir().getProperty((String)key);
        } else {
            return null;
        }
    }
}
