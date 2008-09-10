package org.seasar.kvasir.cms.pop.creator;

import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.assembler.AutoBindingDefFactory;
import org.seasar.framework.container.creator.ComponentCreatorImpl;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.convention.NamingConvention;


public class PopCreator extends ComponentCreatorImpl
{
    public PopCreator(NamingConvention namingConvention)
    {
        super(namingConvention);
        setNameSuffix("Pop");
        setInstanceDef(InstanceDefFactory.PROTOTYPE);
        setAutoBindingDef(AutoBindingDefFactory.AUTO);
        setExternalBinding(false);
    }


    public ComponentCustomizer getPopCustomizer()
    {
        return getCustomizer();
    }


    public void setPopCustomizer(ComponentCustomizer customizer)
    {
        setCustomizer(customizer);
    }
}
