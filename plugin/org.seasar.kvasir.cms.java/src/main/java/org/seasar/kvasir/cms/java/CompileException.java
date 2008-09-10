package org.seasar.kvasir.cms.java;

public class CompileException extends Exception
{
    private static final long serialVersionUID = 2633567056156472091L;


    public CompileException()
    {
    }


    public CompileException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }


    public CompileException(String arg0)
    {
        super(arg0);
    }


    public CompileException(Throwable arg0)
    {
        super(arg0);
    }
}
