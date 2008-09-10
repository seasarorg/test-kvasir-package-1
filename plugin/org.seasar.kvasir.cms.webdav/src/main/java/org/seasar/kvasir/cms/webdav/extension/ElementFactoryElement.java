package org.seasar.kvasir.cms.webdav.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;

import net.skirnir.xom.annotation.Bean;

@Component(bindingType = BindingType.MUST, isa = ElementFactory.class)
@Bean("element-factory")
public class ElementFactoryElement extends AbstractElement
{
}
