package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;


public class ElementClassMetaData
{
    private BindingType binding_ = BindingType.NONE;

    private Class<?> isa_;

    private boolean replace_;


    ElementClassMetaData(Class<?> elementClass, ClassLoader classLoader)
    {
        prepare(elementClass);
    }


    void prepare(Class<?> elementClass)
    {
        Component component = elementClass.getAnnotation(Component.class);
        if (component != null) {
            binding_ = component.bindingType();
            if (component.isa() != Object.class) {
                isa_ = component.isa();
            }
            replace_ = component.replace();
            if (replace_ && isa_ == null) {
                // replace=trueの時は、コンポーネントはisaがなくても最低限ExtensionElement
                // クラス自身である必要があるのでこうしている。
                isa_ = elementClass;
            }
        }
    }


    public Class<?> getIsa()
    {
        return isa_;
    }


    public boolean isReplace()
    {
        return replace_;
    }


    public BindingType getBinding()
    {
        return binding_;
    }
}