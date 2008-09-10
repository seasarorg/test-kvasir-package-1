package org.seasar.kvasir.system.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
class ExtensionElementDependant
    implements Dependant
{
    private ExtensionElement element_;

    private String id_;

    private List<Requirement> requirementList_ = new ArrayList<Requirement>();


    /*
     * constructors
     */

    public ExtensionElementDependant(ExtensionElement element)
    {
        element_ = element;
        id_ = element.getFullId();
    }


    public ExtensionElementDependant(String id)
    {
        id_ = id;
    }


    /*
     * public scope methods
     */

    public ExtensionElement getExtensionElement()
    {
        return element_;
    }


    public void addRequirements(Requirement requirement)
    {
        requirementList_.add(requirement);
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
     * Dependant
     */

    public String getId()
    {
        return id_;
    }


    public boolean isDisabled()
    {
        return false;
    }


    public Requirement[] getRequirements()
    {
        Map<String, Requirement> requirementMap = new LinkedHashMap<String, Requirement>();
        String after = (element_ != null) ? element_.getAfter() : null;
        if (after != null) {
            StringTokenizer st = new StringTokenizer(after, ",");
            while (st.hasMoreTokens()) {
                String id = st.nextToken().trim();
                requirementMap.put(id, new ExtensionElementRequirement(id));
            }
        }
        for (Iterator<Requirement> itr = requirementList_.iterator(); itr
            .hasNext();) {
            Requirement requirement = itr.next();
            requirementMap.put(requirement.getId(), requirement);
        }

        return requirementMap.values().toArray(new Requirement[0]);
    }
}
