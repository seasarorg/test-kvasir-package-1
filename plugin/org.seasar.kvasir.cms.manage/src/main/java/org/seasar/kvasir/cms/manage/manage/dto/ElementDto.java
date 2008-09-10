package org.seasar.kvasir.cms.manage.manage.dto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;


public class ElementDto extends ElementDtoBase
{
    public static final String PARAM_DELIM = ":";

    public static final String PARAM_TYPE_ATTRIBUTE = "(attribute)";

    public static final String PARAM_TYPE_CHILD = "(child)";

    private Object element_;

    private BeanAccessor accessor_;

    private Locale locale_;


    public ElementDto(Object element, BeanAccessor accessor, Locale locale,
        String label, String description, String name, Integer index,
        String styleClass)
    {
        super(label, description, name, index, styleClass);
        element_ = element;
        accessor_ = accessor;
        locale_ = locale;
    }


    public ElementUnitDto[] getAttributes()
    {
        List<ElementUnitDto> list = new ArrayList<ElementUnitDto>();
        String[] names = accessor_.getAttributeNames();
        for (int i = 0; i < names.length; i++) {
            addElementUnitDto(accessor_.getAttributeDescriptor(names[i]), true,
                list);
        }
        names = accessor_.getChildNames();
        for (int i = 0; i < names.length; i++) {
            PropertyDescriptor descriptor = accessor_
                .getChildDescriptor(names[i]);
            addElementUnitDto(descriptor, false, list);
        }
        return list.toArray(new ElementUnitDto[0]);
    }


    void addElementUnitDto(PropertyDescriptor descriptor, boolean attribute,
        List<ElementUnitDto> list)
    {
        if (attribute || descriptor.isScalar()) {
            String name = descriptor.getName();
            Object value;
            try {
                value = (attribute ? accessor_.getAttribute(element_, name)
                    : accessor_.getChild(element_, name));
            } catch (TargetNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            if (descriptor.isMultiple()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    list.add(newElementUnitDto(descriptor, Array.get(value, i),
                        getIndexedName()
                            + PARAM_DELIM
                            + (attribute ? PARAM_TYPE_ATTRIBUTE
                                : PARAM_TYPE_CHILD) + name, Integer.valueOf(i),
                        null, descriptor.isRequired()));
                }
                list.add(newElementUnitDto(descriptor, "", getIndexedName()
                    + PARAM_DELIM
                    + (attribute ? PARAM_TYPE_ATTRIBUTE : PARAM_TYPE_CHILD)
                    + name, Integer.valueOf(length), STYLECLASS_PROTOTYPE,
                    descriptor.isRequired()));
            } else {
                list.add(newElementUnitDto(descriptor, value, getIndexedName()
                    + PARAM_DELIM
                    + (attribute ? PARAM_TYPE_ATTRIBUTE : PARAM_TYPE_CHILD)
                    + name, null, null, descriptor.isRequired()));
            }
        }
    }


    public ElementDto[] getChildren()
    {
        List<ElementDto> list = new ArrayList<ElementDto>();
        String[] names = accessor_.getChildNames();
        for (int i = 0; i < names.length; i++) {
            addElementDto(accessor_.getChildDescriptor(names[i]), list);
        }
        return list.toArray(new ElementDto[0]);
    }


    void addElementDto(PropertyDescriptor descriptor, List<ElementDto> list)
    {
        if (!descriptor.isScalar()) {
            String name = descriptor.getName();
            Object value;
            try {
                value = accessor_.getChild(element_, name);
            } catch (TargetNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            String childName = getIndexedName() + PARAM_DELIM
                + PARAM_TYPE_CHILD + name;
            if (descriptor.isMultiple()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    list.add(newElementDto(descriptor, Array.get(value, i),
                        childName, new Integer(i), null));
                }
                list.add(newElementDto(descriptor, descriptor.getTypeAccessor()
                    .newInstance(), childName, new Integer(length),
                    STYLECLASS_PROTOTYPE));
            } else {
                list
                    .add(newElementDto(descriptor, value, childName, null, null));
            }
        }
    }


    ElementDto newElementDto(PropertyDescriptor descriptor, Object value,
        String name, Integer index, String styleClass)
    {
        return new ElementDto(value, descriptor.getTypeAccessor(), locale_,
            descriptor.getName(), descriptor.getDescription(locale_), name,
            index, styleClass);
    }


    ElementUnitDto newElementUnitDto(PropertyDescriptor descriptor,
        Object value, String name, Integer index, String styleClass,
        boolean required)
    {
        return new ElementUnitDto(descriptor.getName(), PropertyUtils.valueOf(
            value, ""), descriptor.getDescription(locale_), name, index,
            isBoolean(descriptor), styleClass, required);
    }


    boolean isBoolean(PropertyDescriptor descriptor)
    {
        Class<?> type = descriptor.getType();
        return (type == Boolean.TYPE || type == Boolean.class);
    }
}
