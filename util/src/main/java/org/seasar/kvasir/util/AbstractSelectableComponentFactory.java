package org.seasar.kvasir.util;

import java.util.Properties;


/**
 * <p><b>同期化：</b>
 * このクラスはフレームワークによって初期化された後はスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class AbstractSelectableComponentFactory
{
    private Properties          prop_ = new Properties();
    private String              defaultClassName_;


    protected final Object newInstance(String name)
    {
        String className = prop_.getProperty(name);
        if (className == null) {
            className = defaultClassName_;
        }

        return ClassUtils.newInstance(className, true,
            getClass().getClassLoader());
    }


    /*
     * for framework
     */

    public final void setDefaultClassName(String className)
    {
        defaultClassName_ = className;
    }


    public final void setClassName(String productId, String className)
    {
        prop_.setProperty(productId, className);
    }
}
