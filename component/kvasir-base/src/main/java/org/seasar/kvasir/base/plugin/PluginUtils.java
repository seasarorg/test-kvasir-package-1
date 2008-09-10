package org.seasar.kvasir.base.plugin;

import org.seasar.kvasir.base.container.ComponentContainer;


public class PluginUtils
{
    private PluginUtils()
    {
    }


    public static Class<?> getClass(Plugin<?> plugin, String className)
        throws ClassNotFoundException
    {
        if (className == null) {
            return null;
        } else {
            return Class.forName(className, true, plugin.getInnerClassLoader());
        }
    }


    public static Object getComponent(Plugin<?> plugin, Object key)
    {
        if (key == null) {
            return null;
        } else {
            return plugin.getComponentContainer().getComponent(key);
        }
    }


    public static Class<? extends Object> getComponentClass(Plugin<?> plugin,
        String key)
    {
        if (key == null) {
            return null;
        }
        ComponentContainer container = plugin.getComponentContainer();
        if (container.hasComponent(key)) {
            return container.getComponentClass(key);
        } else {
            return null;
        }
    }
}
