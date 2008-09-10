package org.seasar.kvasir.util.dependency;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class MockDependant
    implements Dependant
{
    private String          id_;
    private boolean         disabled_;
    private Requirement[]   requirements_;


    public MockDependant(String id, boolean disabled,
        Requirement[] requirements)
    {
        id_ = id;
        disabled_ = disabled;
        requirements_ = requirements;
    }


    /*
     * Dependant
     * 
     */

    public String getId()
    {
        return id_;
    }


    public boolean isDisabled()
    {
        return disabled_;
    }


    public Requirement[] getRequirements()
    {
        return requirements_;
    }
}
