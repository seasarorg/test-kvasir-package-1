package org.seasar.kvasir.base.plugin.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PluginDependant
    implements Dependant
{
    private PluginDescriptor descriptor_;


    /*
     * constructors
     */

    public PluginDependant(PluginDescriptor descriptor)
    {
        descriptor_ = descriptor;
    }


    /*
     * public scope methods
     */

    public PluginDescriptor getDescriptor()
    {
        return descriptor_;
    }


    public Version getVersion()
    {
        return descriptor_.getVersion();
    }


    /*
     * Object
     */

    @Override
    public String toString()
    {
        return getId();
    }


    /*
     * Dependant
     */

    public String getId()
    {
        return descriptor_.getId();
    }


    public boolean isDisabled()
    {
        return descriptor_.isDisabled();
    }


    public Requirement[] getRequirements()
    {
        Requires requires = descriptor_.getRequires();
        if (requires == null) {
            return new Requirement[0];
        } else {
            Import[] imports = requires.getImports();
            List<PluginRequirement> list = new ArrayList<PluginRequirement>(
                imports.length);
            for (int i = 0; i < imports.length; i++) {
                if (imports[i].getPlugin() == null) {
                    continue;
                }
                list.add(new PluginRequirement(imports[i]));
            }
            return list.toArray(new Requirement[0]);
        }
    }
}
