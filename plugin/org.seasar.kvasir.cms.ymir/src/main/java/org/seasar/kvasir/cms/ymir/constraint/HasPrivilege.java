package org.seasar.kvasir.cms.ymir.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.seasar.cms.ymir.extension.ConstraintType;
import org.seasar.cms.ymir.extension.annotation.ConstraintAnnotation;
import org.seasar.kvasir.page.ability.Privilege;


@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
@ConstraintAnnotation(type = ConstraintType.PERMISSION, factory = HasPrivilegeConstraintFactory.class)
public @interface HasPrivilege
{
    Privilege value() default Privilege.ACCESS_VIEW;
}
