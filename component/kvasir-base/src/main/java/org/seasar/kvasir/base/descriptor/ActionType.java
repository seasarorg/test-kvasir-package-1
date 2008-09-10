package org.seasar.kvasir.base.descriptor;

public enum ActionType
{
    REPLACE("replace"), REMOVE("remove");

    private String name_;


    public static ActionType getActionType(String name)
    {
        if (REPLACE.getName().equals(name)) {
            return REPLACE;
        } else if (REMOVE.getName().equals(name)) {
            return REMOVE;
        } else {
            return null;
        }
    }


    private ActionType(String name)
    {
        name_ = name;
    }


    public String getName()
    {
        return name_;
    }
}
