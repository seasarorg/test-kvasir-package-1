package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;


@Component(bindingType = BindingType.MUST, isa = GeepElement1.class)
public class GeepElement1 extends AbstractElement
{
}
