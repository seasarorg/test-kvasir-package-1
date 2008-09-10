package org.seasar.kvasir.util.dependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class Dependency
{
    public static final int     ERROR_GENERIC = 0;
    public static final int     ERROR_SOURCE_IS_ALREADY_DISABLED = 1;
    public static final int     ERROR_REQUIRED_ELEMENT_DOES_NOT_EXIST = 2;
    public static final int     ERROR_REQUIRED_ELEMENT_VERSION_MISMATCH = 3;

    private Dependencies        dependencies_;
    private Dependant           source_;
    private boolean             disabled_;
    private Dependency[]        requirements_;
    private List                dependantList_;
    private Dependency[]        dependants_;
    private Dependency[]        allDependants_;
    private boolean             checked_;
    private Log                 log_;


    /*
     * constructors
     */

    public Dependency(Dependencies dependencies, Dependant source)
    {
        dependencies_ = dependencies;
        source_ = source;
        if (source_.isDisabled()) {
            log_ = new Log(ERROR_SOURCE_IS_ALREADY_DISABLED);
            setDisabled0();
        }
        dependantList_ = new ArrayList();
    }


    public boolean equals(Object obj)
    {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Dependency d = (Dependency)obj;
        if (d.source_ == null) {
            if (source_ != null) {
                return false;
            } else {
                return true;
            }
        } else {
            if (source_ != null) {
                return d.source_.getId().equals(source_.getId());
            } else {
                return false;
            }
        }
    }


    public int hashCode()
    {
        if (source_ == null) {
            return 0;
        } else {
            return source_.getId().hashCode();
        }
    }


    public String toString()
    {
        return String.valueOf(source_);
    }


    public Dependant getSource()
    {
        return source_;
    }


    public String getId()
    {
        return source_.getId();
    }


    public boolean isDisabled()
    {
        return disabled_;
    }


    public void setDisabled()
    {
        log_ = new Log(ERROR_GENERIC);
        setDisabled0();
        update();
    }


    public Dependency[] getRequirements()
    {
        return requirements_;
    }


    public Dependency[] getDependants()
    {
        return dependants_;
    }


    public Dependency[] getAllDependants()
    {
        return allDependants_;
    }


    public boolean isIndependent()
    {
        return (requirements_.length == 0);
    }


    public Log getLog()
    {
        return log_;
    }


    /*
     * package scope methods
     */

    void construct(Map dependencyMap)
    {
        boolean ignoreNonexistent
            = dependencies_.isIgnoreNonexistentDependency();

        Requirement[] rs = source_.getRequirements();
        List requirementList = new ArrayList(rs.length);
        for (int i = 0; i < rs.length; i++) {
            Requirement r = rs[i];
            Dependency d = (Dependency)dependencyMap.get(r.getId());
            if (d == null) {
                if (ignoreNonexistent) {
                    continue;
                } else {
                    // 依存するDependencyが見つからない。
                    log_ = new Log(ERROR_REQUIRED_ELEMENT_DOES_NOT_EXIST, r);
                    setDisabled0();
                    continue;
                }
            } else if (!r.isMatched(d.getSource())) {
                // 依存するDependencyが見つからない。
                log_ = new Log(ERROR_REQUIRED_ELEMENT_VERSION_MISMATCH,
                    r, d.getSource());
                setDisabled0();
                continue;
            }
            requirementList.add(d);
            d.addDependency(this);
        }
        requirements_ = (Dependency[])requirementList.toArray(
            new Dependency[0]);
    }


    void finishConstruction()
    {
        dependants_ = (Dependency[])dependantList_.toArray(new Dependency[0]);
        dependantList_ = null;
    }


    void addDependency(Dependency d)
    {
        dependantList_.add(d);
    }


    void checkLoop()
        throws LoopDetectedException
    {
        checkLoop(new HashSet());
    }


    Dependency[] gatherAllDependants()
    {
        Set set = gatherAllDependants(new LinkedHashSet());

        final Map map = new LinkedHashMap();
        for (Iterator itr = set.iterator(); itr.hasNext();) {
            Dependency d = (Dependency)itr.next();
            map.put(d, new Integer(getMaxDistance(this, d)));
        }
        allDependants_ = (Dependency[])map.keySet().toArray(new Dependency[0]);
        Arrays.sort(allDependants_, new Comparator() {
            public int compare(Object o1, Object o2)
            {
                return (((Integer)map.get(o1)).intValue()
                    - ((Integer)map.get(o2)).intValue());
            }
        });
        return allDependants_;
    }


    Set gatherAllDependants(Set set)
    {
        for (int i = 0; i < dependants_.length; i++) {
            set.add(dependants_[i]);
            dependants_[i].gatherAllDependants(set);
        }
        return set;
    }


    void clearAllDependants()
    {
        allDependants_ = new Dependency[0];
    }


    void setDisabled0()
    {
        disabled_ = true;
    }


    void update()
    {
        if (disabled_) {
            for (int i = 0; i < dependants_.length; i++) {
                dependants_[i].setDisabled0();
            }
        }
        for (int i = 0; i < dependants_.length; i++) {
            dependants_[i].update();
        }
    }


    /*
     * private scope methods
     */

    private void checkLoop(Set set)
        throws LoopDetectedException
    {
        if (checked_) {
            return;
        }

        for (int i = 0; i < dependants_.length; i++) {
            Dependency d = dependants_[i];
            if (set.contains(d)) {
                throw new LoopDetectedException()
                    .setRequirement(this).setDependant(d);
            }
            set.add(d);
            d.checkLoop(set);
            set.remove(d);
        }
        checked_ = true;
    }


    private int getMaxDistance(Dependency from, Dependency to)
    {
        if (from.equals(to)) {
            return 0;
        }
        int max = -2;
        Dependency[] rs = to.getRequirements();
        for (int i = 0; i < rs.length; i++) {
            int distance = getMaxDistance(from, rs[i]);
            if (distance > max) {
                max = distance;
            }
        }
        return max + 1;
    }


    /*
     * inner classes
     */

    public class Log
    {
        private int             errorCode_;
        private Object          object1_;
        private Object          object2_;

        private Log(int errorCode)
        {
            this(errorCode, null, null);
        }

        private Log(int errorCode, Object object1)
        {
            this(errorCode, object1, null);
        }

        private Log(int errorCode, Object object1, Object object2)
        {
            errorCode_ = errorCode;
            object1_ = object1;
            object2_ = object2;
        }

        public int getErrorCode()
        {
            return errorCode_;
        }

        public Object getObject1()
        {
            return object1_;
        }

        public Object getObject2()
        {
            return object2_;
        }

        public String toString()
        {
            switch (errorCode_) {
            case ERROR_GENERIC:
                return "Generic error";

            case ERROR_SOURCE_IS_ALREADY_DISABLED:
                return "This element is already disabled";

            case ERROR_REQUIRED_ELEMENT_DOES_NOT_EXIST:
                return "Required element does not exist: " + object1_;

            case ERROR_REQUIRED_ELEMENT_VERSION_MISMATCH:
                return "Required element version mismatch: "
                    + "required=" + object1_ + ", found=" + object2_;

            default:
                return "(Unknown error): " + object1_ + ", " + object2_;
            }
        }
    }
}
