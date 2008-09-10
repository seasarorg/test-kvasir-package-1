package org.seasar.kvasir.page.ability;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum PrivilegeLevel
{
    /** 不明なレベルであることを表します。 */
    UNKNOWN(-1),

    /** NONEレベルを表します。 */
    NONE(0),

    /** PEEKレベルを表します。 */
    PEEK(1),

    /** VIEWレベルを表します。 */
    VIEW(2),

    /** COMMENTレベルを表します。 */
    COMMENT(3),

    /** ACCESSレベルを表します。 */
    ACCESS(126),

    /** NEVERレベルを表します。 */
    NEVER(127);

    private static final Map<Integer, PrivilegeLevel> levelByValueMap_;

    private int value_;

    private String name_;

    static {
        Map<Integer, PrivilegeLevel> map = new HashMap<Integer, PrivilegeLevel>();
        PrivilegeLevel[] levels = values();
        for (int i = 0; i < levels.length; i++) {
            map.put(levels[i].getValue(), levels[i]);
        }
        levelByValueMap_ = Collections.unmodifiableMap(map);
    }


    public static PrivilegeLevel getLevel(int value)
    {
        return levelByValueMap_.get(value);
    }


    public static PrivilegeLevel getLevel(int value, PrivilegeLevel defaultLevel)
    {
        PrivilegeLevel level = getLevel(value);
        if (level != null) {
            return level;
        } else {
            return defaultLevel;
        }
    }


    public static PrivilegeLevel getLevel(String name)
    {
        try {
            return Enum.valueOf(PrivilegeLevel.class, name.toUpperCase());
        } catch (Throwable t) {
            return null;
        }
    }


    public static PrivilegeLevel getLevel(String name,
        PrivilegeLevel defaultLevel)
    {
        PrivilegeLevel level = getLevel(name);
        if (level != null) {
            return level;
        } else {
            return defaultLevel;
        }
    }


    PrivilegeLevel(int value)
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
