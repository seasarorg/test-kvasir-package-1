package org.seasar.kvasir.base.xom;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.util.collection.DelegatingI18NPropertyHandler;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;
import org.seasar.kvasir.util.io.impl.JavaResource;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.I18NString;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessor;


/**
 * xproperties形式で用意された説明文リソースを扱えるようにしたAnnotationBeanAccessorクラスです。
 * <p>説明文をBeanと同じパッケージ・同じ名前のリソースファイル（拡張子は.xproperties）に格納しておくことで、
 * BeanAccessor#getDescription(Locale)やPropertyDescriptor#getDescription(Locale)が
 * 説明文を返すようになります。
 * それ以外の挙動はAnnotationBeanAccessorと同じです。
 * </p>
 * <p>xpropertiesファイルには、Beanの説明文を「<code>description</code>」というキーで書いておきます。
 * 属性や子要素の説明文はそれぞれ「<code>属性名.description</code>」「<code>要素名.description</code>」
 * というキーで書いておきます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスは状態の変更を伴う場合スレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirBeanAccessor extends AnnotationBeanAccessor
{
    public static final String PROP_DESCRIPTION = "description";

    public static final String PROP_SUFFIX_DESCRIPTION = ".description";

    private I18NPropertyHandler handler_;


    @SuppressWarnings("unchecked")
    @Override
    public BeanAccessor setBeanClass(Class beanClass)
    {
        handler_ = buildHandler(beanClass);

        return super.setBeanClass(beanClass);
    }


    I18NPropertyHandler buildHandler(Class<?> beanClass)
    {
        List<I18NPropertyHandler> list = new ArrayList<I18NPropertyHandler>();

        Class<?> clazz = beanClass;
        do {
            I18NPropertyHandler hander = buildSingleHandler(clazz);
            if (hander != null) {
                list.add(hander);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null && clazz != Object.class);

        Class<?>[] interfaces = beanClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            I18NPropertyHandler hander = buildSingleHandler(interfaces[i]);
            if (hander != null) {
                list.add(hander);
            }
        }

        return new DelegatingI18NPropertyHandler(list
            .toArray(new I18NPropertyHandler[0]));
    }


    I18NPropertyHandler buildSingleHandler(Class<?> clazz)
    {
        String resourcePath = clazz.getName().replace('.', '/');
        String parentResourcePath;
        String resourceBaseName;
        int slash = resourcePath.lastIndexOf('/');
        if (slash >= 0) {
            parentResourcePath = resourcePath.substring(0, slash);
            resourceBaseName = resourcePath.substring(slash + 1);
        } else {
            parentResourcePath = "";
            resourceBaseName = resourcePath;
        }
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader.getResource(resourcePath + ".xproperties") != null) {
            return new I18NProperties(new JavaResource(parentResourcePath,
                classLoader), resourceBaseName, ".xproperties");
        } else {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    protected I18NString getDescription(Class beanClass)
    {
        return new I18NPropertyHandlerToI18NStringAdapter(handler_,
            PROP_DESCRIPTION);
    }


    @Override
    protected PropertyDescriptor constructAttributeDescriptor(Method method)
    {
        PropertyDescriptor descriptor = super
            .constructAttributeDescriptor(method);
        descriptor.setDescription(new I18NPropertyHandlerToI18NStringAdapter(
            handler_, descriptor.getName() + PROP_SUFFIX_DESCRIPTION));
        return descriptor;
    }


    @Override
    protected PropertyDescriptor constructChildDescriptor(Method method)
    {
        PropertyDescriptor descriptor = super.constructChildDescriptor(method);
        descriptor.setDescription(new I18NPropertyHandlerToI18NStringAdapter(
            handler_, descriptor.getName() + PROP_SUFFIX_DESCRIPTION));
        return descriptor;
    }
}
