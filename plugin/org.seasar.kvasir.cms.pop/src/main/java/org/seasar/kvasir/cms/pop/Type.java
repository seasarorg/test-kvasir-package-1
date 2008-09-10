package org.seasar.kvasir.cms.pop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum Type
{
    UNDEFINED(null), BOOLEAN("boolean"), SELECT("select"), PAGE("page");

    private static final Map<String, Type> byNameMap_;

    private String name_;

    static {
        Map<String, Type> byNameMap = new HashMap<String, Type>();
        Type[] types = values();
        for (int i = 0; i < types.length; i++) {
            byNameMap.put(types[i].getName(), types[i]);
        }
        byNameMap_ = Collections.unmodifiableMap(byNameMap);
    }


    public static Type getType(String name)
    {
        if (name == null) {
            return null;
        } else if (byNameMap_.containsKey(name)) {
            return byNameMap_.get(name);
        } else {
            throw new IllegalArgumentException("Undefined type name: " + name);
        }
    }


    private Type(String name)
    {
        name_ = name;
    }


    public String getName()
    {
        return name_;
    }
}
