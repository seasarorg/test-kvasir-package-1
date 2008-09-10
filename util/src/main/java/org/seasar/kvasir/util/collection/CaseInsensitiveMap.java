package org.seasar.kvasir.util.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class CaseInsensitiveMap implements Map
{
    private Map     map_;
    private Map     nameMap_ = new HashMap();


    public CaseInsensitiveMap(Map map)
    {
        map_ = map;
    }


    public Map getMap()
    {
        return map_;
    }


    public void clear()
    {
        map_.clear();
    }


    public boolean containsKey(Object key)
    {
        return map_.containsKey(intern(key));
    }
    public boolean containsValue(Object value)
    {
        return map_.containsValue(value);
    }
    public Set entrySet()
    {
        return map_.entrySet();
    }
    public boolean equals(Object obj)
    {
        return (this == obj);
    }
    public Object get(Object key)
    {
        return map_.get(intern(key));
    }
    public int hashCode()
    {
        return map_.hashCode();
    }
    public boolean isEmpty()
    {
        return map_.isEmpty();
    }
    public Set keySet()
    {
        return map_.keySet();
    }
    public Object put(Object key, Object value)
    {
        return map_.put(intern(key), value);
    }
    public void putAll(Map t)
    {
        for (Iterator itr = t.entrySet().iterator(); itr.hasNext(); ) {
            Map.Entry entry = (Map.Entry)itr.next();
            map_.put(intern(entry.getKey()), entry.getValue());
        }
    }
    public Object remove(Object key)
    {
        Object obj = map_.remove(intern(key));
        removeFromNameMap(key);
        return obj;
    }
    public int size()
    {
        return map_.size();
    }
    public String toString()
    {
        return map_.toString();
    }
    public Collection values()
    {
        return map_.values();
    }


    /*
     * private scope methods
     */

    private Object intern(Object key)
    {
        if (key instanceof String) {
            String name = (String)key;
            String iname = name.toLowerCase();
            String intern = (String)nameMap_.get(iname);
            if (intern == null) {
                intern = name;
                nameMap_.put(iname, name);
            }
            return intern;
        } else {
            return key;
        }
    }


    private void removeFromNameMap(Object key)
    {
        if (key instanceof String) {
            nameMap_.remove(((String)key).toLowerCase());
        }
    }
}
