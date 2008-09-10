package org.seasar.kvasir.page.auth.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.auth.AuthSystem;

import net.skirnir.xom.annotation.Bean;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = AuthSystem.class)
@Bean("auth-system")
public class AuthSystemElement extends AbstractElement
{
}
