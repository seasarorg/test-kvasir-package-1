package org.seasar.kvasir.util.el.impl;

import java.util.Map;

import org.seasar.kvasir.util.el.VariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスがスレッドセーフであるかどうかは、
 * 内包するMapがスレッドセーフであるかどうかに依存します。
 * 通常はスレッドセーフであるようなMapをコンストラクタに渡すか、
 * 渡したMapの構造を外部から変更しないようにして下さい。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MapVariableResolver
    implements VariableResolver
{
    private Map map_;

    private VariableResolver parent_;


    /**
     * このクラスのオブジェクトを構築します。
     *
     * @param map Mapオブジェクト。
     */
    public MapVariableResolver(Map map)
    {
        this(map, null);
    }


    /**
     * このクラスのオブジェクトを構築します。
     *
     * @param map Mapオブジェクト。
     * @param parent 親VariableResolverオブジェクト。
     * nullを指定することもできます。
     */
    public MapVariableResolver(Map map, VariableResolver parent)
    {
        map_ = map;
        parent_ = parent;
    }


    protected Map getMap()
    {
        return map_;
    }


    /*
     * VariableResolver
     */

    public Object getValue(Object key)
    {
        if (map_.containsKey(key)) {
            return map_.get(key);
        } else if (parent_ != null) {
            return parent_.getValue(key);
        } else {
            return null;
        }
    }
}
