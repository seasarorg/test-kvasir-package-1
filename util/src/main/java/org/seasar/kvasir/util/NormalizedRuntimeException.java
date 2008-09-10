package org.seasar.kvasir.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Stack;


public class NormalizedRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -7349115219781370941L;

    private Throwable cause_;


    public NormalizedRuntimeException(Throwable cause)
    {
        super(cause);
        cause_ = cause;
    }


    public void printStackTrace(PrintWriter s)
    {
        StackTraceEntry[] entries = getStackTraceEntries(cause_);
        for (int i = 0; i < entries.length; i++) {
            if (i == 0) {
                s.println(entries[i].getThrowable());
            } else {
                s.println("Caused by: " + entries[i].getThrowable());
            }
            StackTraceElement[] elements = entries[i].getElements();
            for (int j = 0; j < elements.length; j++) {
                s.println("\tat " + elements[j]);
            }
        }
    }


    public void printStackTrace(PrintStream s)
    {
        StackTraceEntry[] entries = getStackTraceEntries(cause_);
        for (int i = 0; i < entries.length; i++) {
            if (i == 0) {
                s.println(entries[i].getThrowable());
            } else {
                s.println("After: " + entries[i].getThrowable());
            }
            StackTraceElement[] elements = entries[i].getElements();
            for (int j = 0; j < elements.length; j++) {
                s.println("\tat " + elements[j]);
            }
        }
    }


    StackTraceEntry[] getStackTraceEntries(Throwable throwable)
    {
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return new StackTraceEntry[] { new StackTraceEntry(throwable,
                throwable.getStackTrace()) };
        }

        return mergeStackTraceEntries(throwable, getStackTraceEntries(cause));

    }


    StackTraceEntry[] mergeStackTraceEntries(Throwable throwable,
        StackTraceEntry[] entries)
    {
        StackTraceElement[] elements = throwable.getStackTrace();
        Stack elementStack = new Stack();
        for (int i = 0; i < elements.length; i++) {
            elementStack.push(elements[i]);
        }
        StackTraceElement[] causeElements = entries[0].getThrowable()
            .getStackTrace();
        Stack causeElementStack = new Stack();
        for (int i = 0; i < causeElements.length; i++) {
            causeElementStack.push(causeElements[i]);
        }
        while (!causeElementStack.isEmpty() && !elementStack.isEmpty()) {
            StackTraceElement causeElement = (StackTraceElement)causeElementStack
                .peek();
            StackTraceElement element = (StackTraceElement)elementStack.peek();
            if (causeElement.equals(element)) {
                causeElementStack.pop();
                elementStack.pop();
            } else {
                break;
            }
        }
        elements = (StackTraceElement[])elementStack
            .toArray(new StackTraceElement[0]);

        StackTraceEntry[] newEntries = new StackTraceEntry[entries.length + 1];
        newEntries[0] = new StackTraceEntry(throwable, elements);
        System.arraycopy(entries, 0, newEntries, 1, entries.length);

        return newEntries;
    }


    static class StackTraceEntry
    {
        private Throwable throwable_;

        private StackTraceElement[] elements_;


        StackTraceEntry(Throwable throwable, StackTraceElement[] elements)
        {
            throwable_ = throwable;
            elements_ = elements;
        }


        public Throwable getThrowable()
        {
            return throwable_;
        }


        public StackTraceElement[] getElements()
        {
            return elements_;
        }
    }
}
