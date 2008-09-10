package org.seasar.kvasir.test;

import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class TestRequirement
    implements Requirement
{
    private String className_;


    /*
     * constructors
     */
    public TestRequirement(String className)
    {
        className_ = className;
    }


    public String getId()
    {
        return className_;
    }


    public boolean isMatched(Dependant dependant)
    {
        return getId().equals(dependant.getId());
    }
}
