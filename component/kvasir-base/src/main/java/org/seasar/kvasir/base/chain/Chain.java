package org.seasar.kvasir.base.chain;

import java.util.NoSuchElementException;


public interface Chain<P>
{
    void addProcessor(P processor);


    P pollProcessor()
        throws NoSuchElementException;


    boolean isProcessorEmpty();


    void addProcessors(P[] processors);

    boolean isAllProcessorsProcessed();
}
