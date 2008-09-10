package org.seasar.kvasir.page.ability;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum PrivilegeType
{
    /** 不明な権限であることを表します。 */
    UNKNOWN(-1),

    /** アクセス（ACCESS）権限を表します。 */
    ACCESS(0);

    private static final Map<Integer, PrivilegeType> typeByValueMap_;

    private int value_;

    private String name_;

    static {
        Map<Integer, PrivilegeType> map = new HashMap<Integer, PrivilegeType>();
        PrivilegeType[] types = values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getValue(), types[i]);
        }
        typeByValueMap_ = Collections.unmodifiableMap(map);
    }


    public static PrivilegeType getType(int value)
    {
        return typeByValueMap_.get(value);
    }


    public static PrivilegeType getType(int value, PrivilegeType defaultType)
    {
        PrivilegeType type = getType(value);
        if (type != null) {
            return type;
        } else {
            return defaultType;
        }
    }


    public static PrivilegeType getType(String name)
    {
        try {
            return Enum.valueOf(PrivilegeType.class, name.toUpperCase());
        } catch (Throwable t) {
            return null;
        }
    }


    public static PrivilegeType getType(String name, PrivilegeType defaultType)
    {
        PrivilegeType type = getType(name);
        if (type != null) {
            return type;
        } else {
            return defaultType;
        }
    }


    PrivilegeType(int value)
    {
        value_ = value;
        name_ = name().toLowerCase();
    }


    public int getValue()
    {
        return value_;
    }


    public String getName()
    {
        return name_;
    }


    public String toString()
    {
        return getName();
    }
}
