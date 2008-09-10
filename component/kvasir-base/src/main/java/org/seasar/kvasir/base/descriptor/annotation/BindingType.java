package org.seasar.kvasir.base.descriptor.annotation;

public enum BindingType
{
    NONE, MAY, MUST;

    public String getName()
    {
        return toString().toLowerCase();
    }
}
