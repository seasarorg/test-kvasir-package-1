package org.seasar.kvasir.cms.ymir.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.seasar.cms.ymir.extension.ConstraintType;
import org.seasar.cms.ymir.extension.annotation.ConstraintAnnotation;


@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
@ConstraintAnnotation(type = ConstraintType.PERMISSION, factory = InRoleConstraintFactory.class)
public @interface InRole
{
    String[] value() default {};
}
