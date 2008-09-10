package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;

import net.skirnir.xom.annotation.Bean;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
@Bean("page-ability-alfr")
@Component(bindingType = BindingType.MUST, isa = PageAbilityAlfr.class)
public class PageAbilityAlfrElement extends AbstractElement
{
}
