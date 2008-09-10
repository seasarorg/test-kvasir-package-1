package org.seasar.kvasir.base.extension;

import org.seasar.kvasir.base.KvasirLifecycleListener;
import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Bean;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean("kvasir-lifecycle-listener")
@Component(bindingType = BindingType.MUST, isa = KvasirLifecycleListener.class)
public class KvasirLifecycleListenerElement extends AbstractElement
{
}
