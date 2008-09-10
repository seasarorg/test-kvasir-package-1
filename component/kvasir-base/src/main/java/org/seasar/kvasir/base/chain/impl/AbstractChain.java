package org.seasar.kvasir.base.chain.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.seasar.kvasir.base.chain.Chain;


abstract public class AbstractChain<P>
    implements Chain<P>
{
    private LinkedList<P> processorList_ = new LinkedList<P>();

    private boolean allProcessorsProcessed_;


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (Iterator<P> itr = processorList_.iterator(); itr.hasNext();) {
            sb.append(delim).append(itr.next());
            delim = " -> ";
        }
        return sb.toString();
    }


    public void addProcessor(P processor)
    {
        processorList_.add(processor);
    }


    public boolean isProcessorEmpty()
    {
        return processorList_.isEmpty();
    }


    public P pollProcessor()
        throws NoSuchElementException
    {
        return processorList_.remove();
    }


    public void addProcessors(P[] processors)
    {
        processorList_.addAll(Arrays.asList(processors));
    }


    public boolean isAllProcessorsProcessed()
    {
        return allProcessorsProcessed_;
    }


    protected void setAllProcessorsProcessed(boolean allProcessorsProcessed)
    {
        allProcessorsProcessed_ = allProcessorsProcessed;
    }
}
