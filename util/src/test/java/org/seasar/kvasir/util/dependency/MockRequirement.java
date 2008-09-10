package org.seasar.kvasir.util.dependency;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class MockRequirement
    implements Requirement
{
    private String          id_;


    public MockRequirement(String id)
    {
        id_ = id;
    }


    /*
     * Requirement
     */

    public String getId()
    {
        return id_;
    }


    public boolean isMatched(Dependant dependant)
    {
        if (dependant == null) {
            return false;
        }
        return dependant.getId().equals(id_);
    }

}
