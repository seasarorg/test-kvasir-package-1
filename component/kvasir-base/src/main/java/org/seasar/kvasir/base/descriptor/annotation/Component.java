package org.seasar.kvasir.base.descriptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * plugin.xmlのextensionタグで指定される拡張要素指定とオブジェクトのマッピング規則を指定するための
 * アノテーションです。
 * <p>このアノテーションは拡張要素指定に対応するExtensionElementクラスに対して付与することができます。
 * </p>
 * <p>プラグイン管理機構はplugin.xmlのextensionタグから読み取った拡張要素指定に対応する
 * オブジェクトを決定し（場合によっては生成し）、
 * 拡張要素指定が持つ値をオブジェクトにセットします。
 * Plguin#getExtensionObjects(String)メソッドが返すオブジェクト列は、
 * 拡張要素指定の持つ値がセットされた後のオブジェクトになります。
 * </p>
 * <p>拡張要素指定とオブジェクトのマッピング規則は以下のようになります：</p>
 * <dl>
 * <dt>bindingTypeがBindingType.NONEである場合：</dt>
 * <dd>拡張ポイントの拡張要素クラスのインスタンスを生成して対象オブジェクトとします。</dd>
 * <dt>bindingTypeがBindingType.MAYである場合：</dt>
 * <dd>拡張要素指定がid属性を持つ場合は
 *     id属性の値をキーとしてComponentContainerから取得したコンポーネントを対象オブジェクトとします。
 *     拡張要素指定がid属性を持たない場合は
 *     拡張ポイントの拡張要素クラスのインスタンスを生成して対象オブジェクトとします。</dd>
 * <dt>bindingTypeがBindingType.MUSTである場合：</dt>
 * <dd>拡張要素指定がid属性を持つ場合はIllegalArgumentExceptionがスローされます。</dd>
 * </dl>
 * <p>ComponentContainerから取得したコンポーネントを対象オブジェクトとする場合、
 * コンポーネントが見つからない場合はComponentNotFoundRuntimeExceptionがスローされます。
 * また、isaが指定されている場合は
 * コンポーネントはisaで指定されているクラスのインスタンスである必要があります。
 * そうでない場合はIllegalArgumentExceptionがスローされます。
 * </p>
 * <p>対象オブジェクトには拡張要素指定で指定された値がセットされますが、
 * 対象オブジェクトが拡張ポイントの拡張要素クラスまたはそのサブクラスのインスタンスでない場合は、
 * 対象オブジェクトに拡張要素クラスのインスタンスをセットするためのSetterメソッドが存在すれば
 * それを使って拡張要素クラスのインスタンスが設定されます。
 * 使われるSetterメソッドは引数の型が拡張要素クラスであるようなメソッドで、
 * 渡される値は拡張要素指定の持つ値がセットされている拡張要素クラスのインスタンスになります。
 * 対象オブジェクトのクラスが該当するSetterメソッドを持たない場合は拡張要素クラスのインスタンスは設定されません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component
{
    /**
     * 拡張要素指定をコンポーネントに結びつける方式です。
     * @return 拡張要素指定をコンポーネントに結びつける方式。
     */
    BindingType bindingType() default BindingType.MAY;


    /**
     * 拡張要素指定をコンポーネントに結びつける方式です。
     * @return 拡張要素指定をコンポーネントに結びつける方式。
     */
    Class<?> isa() default Object.class;


    boolean replace() default false;
}
