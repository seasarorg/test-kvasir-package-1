package org.seasar.kvasir.base.plugin.impl;

import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PluginRequirement
    implements Requirement
{
    private Import import_;


    /*
     * constructors
     */

    public PluginRequirement(Import imports)
    {
        import_ = imports;
    }


    /*
     * Object
     */

    @Override
    public String toString()
    {
        return String.valueOf(import_);
    }


    /*
     * Requirement
     */

    public String getId()
    {
        return import_.getPlugin();
    }


    public boolean isMatched(Dependant dependant)
    {
        PluginDependant pd = (PluginDependant)dependant;
        if (!getId().equals(pd.getId())) {
            return false;
        }

        return import_.isMatched(pd.getVersion());
    }
}
