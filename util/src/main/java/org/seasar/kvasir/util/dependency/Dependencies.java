package org.seasar.kvasir.util.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class Dependencies
{
    private boolean             ignoreNonexistentDependency_;
    private Dependency[]        independents_;
    private Map                 dependencyMap_;


    /*
     * constructors
     */

    public Dependencies(Collection collection)
        throws LoopDetectedException
    {
        this(collection, false);
    }


    public Dependencies(Collection collection,
        boolean ignoreNonexistentDependency)
            throws LoopDetectedException
    {
        ignoreNonexistentDependency_ = ignoreNonexistentDependency;

        Map dependencyMap = new LinkedHashMap(); 
        for (Iterator itr = collection.iterator(); itr.hasNext(); ) {
            Dependant dependant = (Dependant)itr.next();
            dependencyMap.put(dependant.getId(),
                new Dependency(this, dependant));
        }
        Dependency[] ds = (Dependency[])dependencyMap.values().toArray(
            new Dependency[0]);

        for (int i = 0; i < ds.length; i++) {
            ds[i].construct(dependencyMap);
        }

        for (int i = 0; i < ds.length; i++) {
            ds[i].finishConstruction();
        }

        for (int i = 0; i < ds.length; i++) {
            ds[i].checkLoop();
        }

        Set set = new LinkedHashSet();
        for (int i = 0; i < ds.length; i++) {
            set.add(ds[i]);
        }
        List independentList = new ArrayList(ds.length);
        dependencyMap = new LinkedHashMap();
        while (set.size() > 0) {
            for (Iterator itr = set.iterator(); itr.hasNext(); ) {
                Dependency d = (Dependency)itr.next();
                if (d.isIndependent()) {
                    d.update();
                    independentList.add(d);
                } else {
                    Dependency[] requirements = d.getRequirements();
                    boolean satisfied = true;
                    for (int i = 0; i < requirements.length; i++) {
                        if (!dependencyMap.containsKey(
                        requirements[i].getId())) {
                            satisfied = false;
                            break;
                        }
                    }
                    if (!satisfied) {
                        continue;
                    }
                }
                d.gatherAllDependants();
                dependencyMap.put(d.getId(), d);
                itr.remove();
                break;
            }
        }
        independents_ = (Dependency[])independentList.toArray(
            new Dependency[0]);
        dependencyMap_ = dependencyMap;
    }


    /*
     * public scope methods
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        boolean multiple = false;
        for (Iterator itr = dependencyMap_.values().iterator();
        itr.hasNext(); ) {
            if (multiple) {
                sb.append(", ");
            } else {
                multiple = true;
            }
            sb.append(itr.next());
        }
        sb.append(" ]");
        return sb.toString();
    }


    public boolean isIgnoreNonexistentDependency()
    {
        return ignoreNonexistentDependency_;
    }


    public Dependency[] getDependencies()
    {
        return (Dependency[])dependencyMap_.values().toArray(
            new Dependency[0]);
    }


    public Dependency getDependency(String id)
    {
        return (Dependency)dependencyMap_.get(id);
    }


    public Dependency[] getIndependents()
    {
        return independents_;
    }
}
