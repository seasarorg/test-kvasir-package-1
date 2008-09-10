package org.seasar.kvasir.cms.ymir.constraint;

import java.lang.reflect.AnnotatedElement;

import org.seasar.cms.ymir.Constraint;
import org.seasar.cms.ymir.extension.constraint.ConstraintFactory;
import org.seasar.cms.ymir.extension.constraint.ConstraintFactoryBase;
import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.page.auth.AuthPlugin;


public class HasPrivilegeConstraintFactory extends ConstraintFactoryBase
    implements ConstraintFactory<HasPrivilege>
{
    public Constraint getConstraint(HasPrivilege annotation,
        AnnotatedElement element)
    {
        return new HasPrivilegeConstraint(annotation.value(),
            getPagAuthPlugin());
    }


    AuthPlugin getPagAuthPlugin()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(
            AuthPlugin.class);
    }
}
