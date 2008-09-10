package org.seasar.kvasir.cms.pop.extension;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;
import org.seasar.kvasir.cms.pop.Kind;


public class FormUnitElementTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        FormUnitElement target = new FormUnitElement();
        target.setKindString(Kind.PROPERTY.getName());

        assertBeanEquals("<form-unit kind=\"property\" />", target);
    }
}
