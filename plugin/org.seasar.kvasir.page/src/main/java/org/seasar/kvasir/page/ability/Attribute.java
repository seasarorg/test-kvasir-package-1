package org.seasar.kvasir.page.ability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.seasar.kvasir.util.collection.CaseInsensitiveMap;
import org.seasar.kvasir.util.io.InputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
public class Attribute
{
    private Map<String, String>             stringMap_ = (Map<String, String>)new CaseInsensitiveMap(
                                                           new HashMap());

    private Map<String, InputStreamFactory> streamMap_ = (Map<String, InputStreamFactory>)new CaseInsensitiveMap(
                                                           new HashMap());


    public Attribute()
    {
    }


    @Override
    public boolean equals(Object o)
    {
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        Attribute attr = (Attribute)o;

        for (Iterator<String> itr = stringNames(); itr.hasNext();) {
            String name = itr.next();
            if (!attr.stringExists(name)) {
                return false;
            }
            String value = getString(name);
            String attrValue = attr.getString(name);
            if (value == null) {
                if (attrValue != null) {
                    return false;
                }
            } else {
                if (attrValue == null) {
                    return false;
                } else if (!value.equals(attrValue)) {
                    return false;
                }
            }
        }

        for (Iterator<String> itr = attr.stringNames(); itr.hasNext();) {
            String name = itr.next();
            if (!stringExists(name)) {
                return false;
            }
        }

        for (Iterator<String> itr = streamNames(); itr.hasNext();) {
            String name = itr.next();
            if (!attr.streamExists(name)) {
                return false;
            }
            InputStreamFactory value = getStream(name);
            InputStreamFactory attrValue = attr.getStream(name);
            if (value == null) {
                if (attrValue != null) {
                    return false;
                }
            } else {
                if (attrValue == null) {
                    return false;
                } else if (!value.equals(attrValue)) {
                    return false;
                }
            }
        }

        for (Iterator<String> itr = attr.streamNames(); itr.hasNext();) {
            String name = itr.next();
            if (!streamExists(name)) {
                return false;
            }
        }

        return true;
    }


    public boolean streamExists(String name)
    {
        return streamMap_.containsKey(name);
    }


    public boolean stringExists(String name)
    {
        return stringMap_.containsKey(name);
    }


    public InputStreamFactory getStream(String name)
    {
        return streamMap_.get(name);
    }


    public String getString(String name)
    {
        return stringMap_.get(name);
    }


    public void setStream(String name, InputStreamFactory value)
    {
        streamMap_.put(name.intern(), value);
    }


    public void setString(String name, String value)
    {
        stringMap_.put(name.intern(), value);
    }


    public void removeStream(String name)
    {
        streamMap_.remove(name);
    }


    public void removeString(String name)
    {
        stringMap_.remove(name);
    }


    public Iterator<String> streamNames()
    {
        return streamMap_.keySet().iterator();
    }


    public Iterator<String> stringNames()
    {
        return stringMap_.keySet().iterator();
    }
}
