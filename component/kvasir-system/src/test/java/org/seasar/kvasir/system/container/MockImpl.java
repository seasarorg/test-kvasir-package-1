package org.seasar.kvasir.system.container;

/**
 * @author YOKOTA Takehiko
 */
public class MockImpl
    implements Mock
{
    private String message_;


    public MockImpl(String message)
    {
        message_ = message;
    }


    @Override
    public String toString()
    {
        return message_;
    }
}
