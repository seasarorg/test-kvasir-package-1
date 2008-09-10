package org.seasar.kvasir.cms.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.filter.PageFilterLifecycleListener;

import net.skirnir.xom.annotation.Bean;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean("page-filter-lifecycle-listener")
@Component(bindingType = BindingType.MUST, isa = PageFilterLifecycleListener.class)
public class PageFilterLifecycleListenerElement extends AbstractElement
{
}
