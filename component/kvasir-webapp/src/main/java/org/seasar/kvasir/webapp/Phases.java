package org.seasar.kvasir.webapp;

public interface Phases
{
    int getOrdinal(String phase)
        throws IllegalArgumentException;
}
