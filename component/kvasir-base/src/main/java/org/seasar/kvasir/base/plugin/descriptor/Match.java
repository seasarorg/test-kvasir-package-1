package org.seasar.kvasir.base.plugin.descriptor;

public enum Match
{
    PERFECT("perfect"), EQUIVALENT("equivalent"), COMPATIBLE("compatible"), GREATER_OR_EQUAL(
        "greaterOrEqual");

    private String name_;


    public static Match getMatch(String name)
    {
        if (PERFECT.getName().equals(name)) {
            return PERFECT;
        } else if (EQUIVALENT.getName().equals(name)) {
            return EQUIVALENT;
        } else if (COMPATIBLE.getName().equals(name)) {
            return COMPATIBLE;
        } else if (GREATER_OR_EQUAL.getName().equals(name)) {
            return GREATER_OR_EQUAL;
        } else {
            return null;
        }
    }


    private Match(String name)
    {
        name_ = name;
    }


    public String getName()
    {
        return name_;
    }
}
