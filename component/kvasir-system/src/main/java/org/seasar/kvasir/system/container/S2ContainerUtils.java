package org.seasar.kvasir.system.container;

import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.impl.ComponentDefImpl;


/**
 * @author YOKOTA Takehiko
 */
public class S2ContainerUtils
{
    private S2ContainerUtils()
    {
    }


    public static ComponentDef copyComponentDefWithoutClassAndName(
        ComponentDef originalCd, Class<?> clazz, String name)
    {
        if (name == null) {
            name = originalCd.getComponentName();
        }
        ComponentDef cd = new ComponentDefImpl(clazz, name);
        cd.setAutoBindingDef(originalCd.getAutoBindingDef());
        cd.setContainer(originalCd.getContainer());
        cd.setExpression(originalCd.getExpression());
        cd.setInstanceDef(originalCd.getInstanceDef());
        int size = originalCd.getArgDefSize();
        for (int i = 0; i < size; i++) {
            cd.addArgDef(originalCd.getArgDef(i));
        }
        size = originalCd.getAspectDefSize();
        for (int i = 0; i < size; i++) {
            cd.addAspectDef(originalCd.getAspectDef(i));
        }
        size = originalCd.getDestroyMethodDefSize();
        for (int i = 0; i < size; i++) {
            cd.addDestroyMethodDef(originalCd.getDestroyMethodDef(i));
        }
        size = originalCd.getInitMethodDefSize();
        for (int i = 0; i < size; i++) {
            cd.addInitMethodDef(originalCd.getInitMethodDef(i));
        }
        size = originalCd.getMetaDefSize();
        for (int i = 0; i < size; i++) {
            cd.addMetaDef(originalCd.getMetaDef(i));
        }
        size = originalCd.getPropertyDefSize();
        for (int i = 0; i < size; i++) {
            cd.addPropertyDef(originalCd.getPropertyDef(i));
        }
        return cd;
    }


    public static ComponentDef copyComponentDefWithoutClass(
        ComponentDef originalCd, Class<?> clazz)
    {
        return copyComponentDefWithoutClassAndName(originalCd, clazz, null);
    }
}
