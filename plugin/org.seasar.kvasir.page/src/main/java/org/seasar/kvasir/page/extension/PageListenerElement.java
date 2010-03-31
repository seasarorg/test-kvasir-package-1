package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.PageListener;

import net.skirnir.xom.annotation.Bean;


/**
 * Pageの状態が変化した際に実行する処理を追加するための拡張ポイントである
 * org.seasar.kvasir.page.pageListenersにプラグインする拡張を表す要素です。
 * 
 * @author YOKOTA Takehiko
 */
@Bean("page-listener")
@Component(bindingType = BindingType.MUST, isa = PageListener.class)
public class PageListenerElement extends AbstractElement
{
}
