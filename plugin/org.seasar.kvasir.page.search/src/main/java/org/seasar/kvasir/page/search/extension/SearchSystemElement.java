package org.seasar.kvasir.page.search.extension;

import org.seasar.kvasir.base.descriptor.AbstractGenericElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.search.SearchSystem;

import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = SearchSystem.class)
@Bean("search-system")
public class SearchSystemElement extends AbstractGenericElement
{
}
