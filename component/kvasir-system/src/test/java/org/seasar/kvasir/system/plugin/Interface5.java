package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;

import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Bean(implementation=Component5.class)
@Component(bindingType=BindingType.MUST)
public interface Interface5
    extends ExtensionElement
{
}
