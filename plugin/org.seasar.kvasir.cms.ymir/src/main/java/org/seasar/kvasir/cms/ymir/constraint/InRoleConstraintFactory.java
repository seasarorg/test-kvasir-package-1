package org.seasar.kvasir.cms.ymir.constraint;

import java.lang.reflect.AnnotatedElement;

import org.seasar.cms.ymir.Constraint;
import org.seasar.cms.ymir.extension.constraint.ConstraintFactory;
import org.seasar.cms.ymir.extension.constraint.ConstraintFactoryBase;
import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;


public class InRoleConstraintFactory extends ConstraintFactoryBase
    implements ConstraintFactory<InRole>
{
    public Constraint getConstraint(InRole annotation, AnnotatedElement element)
    {
        if (annotation.value().length == 0) {
            throw new IllegalArgumentException(
                "Please specify at least one path: " + element);
        }

        return new InRoleConstraint(annotation.value(), getPageAlfr(),
            getPagAuthPlugin());
    }


    AuthPlugin getPagAuthPlugin()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(
            AuthPlugin.class);
    }


    PageAlfr getPageAlfr()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(PagePlugin.class)
            .getPageAlfr();
    }
}
