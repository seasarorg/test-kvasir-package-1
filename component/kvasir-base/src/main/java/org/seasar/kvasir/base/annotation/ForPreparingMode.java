package org.seasar.kvasir.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * システムの準備モードにおいてフレームワークから利用されることを表すアノテーションです。
 * <p>このアノテーションがついているものは、
 * システムのサービス提供前の開始処理やサービス提供後の終了処理のような、
 * システムのサービス準備モードにおいてのみ使用されます。
 * なお準備モードはシングルスレッドであることが保証されています。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ForPreparingMode
{
}
