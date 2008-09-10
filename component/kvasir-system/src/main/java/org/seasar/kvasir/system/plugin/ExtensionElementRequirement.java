package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
class ExtensionElementRequirement
    implements Requirement
{
    private String id_;


    /*
     * constructors
     */

    public ExtensionElementRequirement(String id)
    {
        id_ = id;
    }


    /*
     * Object
     */

    @Override
    public String toString()
    {
        return id_;
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
        return id_.equals(dependant.getId());
    }
}
