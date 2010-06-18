package org.seasar.kvasir.cms.ymir.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.seasar.cms.ymir.extension.ConstraintType;
import org.seasar.cms.ymir.extension.annotation.ConstraintAnnotation;


/**
 * 現在のユーザが指定されたロールを持つ必要があることを表わすアノテーションです。
 * 
 * @author skirnir
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
@ConstraintAnnotation(type = ConstraintType.PERMISSION, factory = InRoleConstraintFactory.class)
public @interface InRole
{
    /**
     * ロールのパス名です。
     * <p>パス名としては絶対パスと相対パスを指定することができます。
     * </p>
     * <p>絶対パスは「<code>/</code>」で始まるパスで、
     * リクエストされたHeim上の絶対パスの位置に存在するロールを表わします。
     * </p>
     * <p>相対パスは「<code>@/</code>」で始まるパスで、
     * リクエストされたページ（存在しない場合はリクエストパスの直近のページ）
     * の属するLordからの相対パス位置に存在するロールを表わします。
     * </p>
     * <p>パスで指定されたロールが存在しない場合には実行時に
     * {@link IllegalArgumentException}がスローされます。
     * </p>
     * <p>複数のパスを指定した場合は、いずれかのロールに属していることを表わします。
     * </p>
     * 
     * @return ロールのパス名。
     */
    String[] value() default {};
}
