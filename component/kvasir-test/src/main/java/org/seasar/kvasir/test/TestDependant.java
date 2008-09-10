package org.seasar.kvasir.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Requirement;


/**
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class TestDependant
    implements Dependant
{
    public static final String FIELD_DEPENDENCIES = "DEPENDENCIES";

    private Class<?> class_;


    /*
     * constructors
     */

    public TestDependant(Class<?> clazz)
    {
        class_ = clazz;
    }


    /*
     * public scope methods
     */

    public Class<?> getTestClass()
    {
        return class_;
    }


    /*
     * Dependant
     */

    public String getId()
    {
        return class_.getName();
    }


    public boolean isDisabled()
    {
        return false;
    }


    public Requirement[] getRequirements()
    {
        String pkg = class_.getPackage().getName();
        Field field;
        try {
            field = class_.getField(FIELD_DEPENDENCIES);
        } catch (NoSuchFieldException ex) {
            return new Requirement[0];
        }
        String dependencies;
        try {
            dependencies = (String)field.get(null);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Can't access " + FIELD_DEPENDENCIES
                + " field", ex);
        }
        List<TestRequirement> list = new ArrayList<TestRequirement>();
        StringTokenizer st = new StringTokenizer(dependencies, ",");
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken().trim();
            if (tkn.length() > 0) {
                list.add(new TestRequirement(pkg + "." + tkn));
            }
        }
        return list.toArray(new Requirement[0]);
    }
}
