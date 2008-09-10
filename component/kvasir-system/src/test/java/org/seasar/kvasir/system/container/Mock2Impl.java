package org.seasar.kvasir.system.container;

/**
 * @author YOKOTA Takehiko
 */
public class Mock2Impl
    implements Mock2
{
    private Mock mock_;


    public Mock getMock()
    {
        return mock_;
    }


    public void setMock(Mock mock)
    {
        mock_ = mock;
    }
}
