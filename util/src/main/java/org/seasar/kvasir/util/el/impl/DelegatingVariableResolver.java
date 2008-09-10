package org.seasar.kvasir.util.el.impl;

import org.seasar.kvasir.util.el.VariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスがスレッドセーフであるかどうかは、
 * 内包するVariableResolverがスレッドセーフであるかどうかに依存します。
 * 通常はスレッドセーフであるようなVariableResolver
 * をコンストラクタに渡すようにして下さい。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class DelegatingVariableResolver
    implements VariableResolver
{
    private VariableResolver[] vrs_;


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param vrs VariableResolverオブジェクトの配列。
     * null要素を含んでいたりnullであったりしてはいけません。
     */
    public DelegatingVariableResolver(VariableResolver[] vrs)
    {
        vrs_ = vrs;
    }


    /*
     * VariableResolver
     */

    public Object getValue(Object key)
    {
        for (int i = 0; i < vrs_.length; i++) {
            Object value = vrs_[i].getValue(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
