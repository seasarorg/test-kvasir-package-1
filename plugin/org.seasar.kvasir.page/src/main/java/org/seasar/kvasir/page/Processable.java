package org.seasar.kvasir.page;

public interface Processable<R>
{
    R process()
        throws ProcessableRuntimeException;
}
