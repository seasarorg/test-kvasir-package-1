package org.seasar.kvasir.cms.pop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum Kind
{
    UNDEFINED(null), PROPERTY("property"), ACTION("action"), GROUP("group");

    private static final Map<String, Kind> byNameMap_;

    private String name_;

    static {
        Map<String, Kind> byNameMap = new HashMap<String, Kind>();
        Kind[] types = values();
        for (int i = 0; i < types.length; i++) {
            byNameMap.put(types[i].getName(), types[i]);
        }
        byNameMap_ = Collections.unmodifiableMap(byNameMap);
    }


    public static Kind getKind(String name)
    {
        if (name == null) {
            return null;
        } else if (byNameMap_.containsKey(name)) {
            return byNameMap_.get(name);
        } else {
            throw new IllegalArgumentException("Undefined kind name: " + name);
        }
    }


    private Kind(String name)
    {
        name_ = name;
    }


    public String getName()
    {
        return name_;
    }
}
