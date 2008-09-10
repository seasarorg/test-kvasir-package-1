package org.seasar.kvasir.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * テストのために利用されることを表すアノテーションです。
 * <p>このアノテーションがついているものは、
 * テストコードからのみ利用されます。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ForTest
{
}
