package org.seasar.kvasir.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.kvasir.util.io.IORuntimeException;


/**
 * @author YOKOTA Takehiko
 */
public class ClassUtils
{
    private static final String PROTOCOL_FILE = "file";

    private static Reference interfaceMapByClassMapRef_ = new SoftReference(
        null);


    private ClassUtils()
    {
    }


    public static Class forName(String className)
    {
        if (className == null) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }


    public static Class forName(String className, boolean initialize,
        ClassLoader loader)
    {
        if (className == null) {
            return null;
        }
        if (loader == null) {
            return forName(className);
        }
        try {
            return Class.forName(className, initialize, loader);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }


    public static Object newInstance(String className)
    {
        return newInstance(forName(className));
    }


    public static Object newInstance(String className, boolean initialize,
        ClassLoader loader)
    {
        return newInstance(forName(className, initialize, loader));
    }


    public static Object newInstance(Class clazz)
    {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException ex) {
            return null;
        } catch (InstantiationException ex) {
            return null;
        }
    }


    public static URL getURLForURLClassLoader(URL url)
    {
        if (url == null) {
            return null;
        }
        if (url.getPath().toLowerCase().endsWith(".jar")) {
            try {
                return new URL("jar:" + url.toExternalForm() + "!/");
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return url;
        }
    }


    public static URL getURLForURLClassLoader(File file)
    {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        URL url;
        try {
            url = file.getCanonicalFile().toURI().toURL();
        } catch (IOException ex) {
            return null;
        }
        if (!file.isDirectory()
            && file.getName().toLowerCase().endsWith(".jar")) {
            try {
                return new URL("jar:" + url.toExternalForm() + "!/");
            } catch (MalformedURLException ex) {
                return null;
            }
        } else {
            return url;
        }
    }


    public static File getFileOfResource(URL url)
    {
        if (url == null) {
            return null;
        }
        if (!PROTOCOL_FILE.equals(url.getProtocol())) {
            return null;
        }
        try {
            return new File(URLDecoder.decode(url.getPath(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Can't happen!", ex);
        } catch (Throwable t) {
            // ログに吐こう。
            return null;
        }
    }


    public static File getParentDirectory(Class clazz)
    {
        String path = clazz.getName().replace('.', '/') + ".class";
        File file = getFileOfResource(clazz.getClassLoader().getResource(path));
        if (file != null) {
            return file.getParentFile();
        } else {
            return null;
        }
    }


    public static File getBaseDirectory(Class clazz)
    {
        File file = getParentDirectory(clazz);
        if (file == null) {
            return null;
        }

        String className = clazz.getName();
        int idx;
        int pre = 0;
        while ((idx = className.indexOf('.', pre)) >= 0) {
            file = file.getParentFile();
            pre = idx + 1;
        }
        return file;
    }


    public static String getId(Object obj, Class iface)
    {
        // XXX memory leakを起こさないためにはこれで良いか？
        synchronized (interfaceMapByClassMapRef_) {
            boolean replace = false;
            Map interfaceMapByClassMap = (Map)interfaceMapByClassMapRef_.get();
            if (interfaceMapByClassMap == null) {
                interfaceMapByClassMap = new HashMap();
                replace = true;
            }

            String id = null;
            if (obj != null) {
                Class clazz = obj.getClass();
                Map ifaceMap = (Map)interfaceMapByClassMap.get(clazz);
                if (ifaceMap != null) {
                    id = (String)ifaceMap.get(iface);
                } else {
                    ifaceMap = new HashMap();
                    interfaceMapByClassMap.put(clazz, ifaceMap);
                }
                if (id == null) {
                    Class[] ifaces = clazz.getInterfaces();
                    for (int i = 0; i < ifaces.length; i++) {
                        if (iface.isAssignableFrom(ifaces[i])) {
                            id = ifaces[i].getName();
                            ifaceMap.put(iface, id);
                            break;
                        }
                    }
                }
            }
            if (id == null) {
                id = String.valueOf(null);
            }

            if (replace) {
                interfaceMapByClassMapRef_ = new SoftReference(
                    interfaceMapByClassMap);
            }

            return id;
        }
    }


    public static void setProperty(Object obj, Object value)
    {
        setProperty(obj, value, false, false);
    }


    public static void setProperty(Object obj, Object value, boolean exact,
        boolean onlyInterface)
    {
        if (obj == null || value == null) {
            return;
        }
        Class clazz = obj.getClass();
        Class paramClass = value.getClass();
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            Class[] types = method.getParameterTypes();
            if (types.length != 1) {
                continue;
            } else if (exact && !types[0].equals(paramClass)) {
                continue;
            } else if (!types[0].isAssignableFrom(paramClass)
                || types[0].equals(Object.class) || onlyInterface
                && !types[0].isInterface()) {
                continue;
            }
            try {
                method.invoke(obj, new Object[] { value });
            } catch (IllegalAccessException ex) {
                throw new IORuntimeException("Can't set property '" + value
                    + "' to " + obj + " with method '" + method + "'", ex);
            } catch (InvocationTargetException ex) {
                throw new IORuntimeException("Can't set property '" + value
                    + "' to " + obj + " with method '" + method + "'", ex);
            }
        }
    }


    public static Class getSubInterface(Class clazz, Class superInterface)
    {
        Class c = clazz;
        while (c != Object.class) {
            Class[] interfaces = c.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (superInterface.isAssignableFrom(interfaces[i])) {
                    return interfaces[i];
                }
            }
            c = c.getSuperclass();
        }
        return null;
    }


    public static Object getAnnotation(Object obj, String name)
    {
        if (obj == null) {
            return null;
        }

        Class objClass;
        Object target;
        if (obj instanceof Class) {
            objClass = (Class)obj;
            target = null;
        } else {
            objClass = obj.getClass();
            target = obj;
        }
        try {
            Field field = objClass.getField(name);
            return field.get(target);
        } catch (Throwable t) {
            return null;
        }
    }


    public static Object[] getAnnotations(Class clazz, String name)
    {
        if (clazz == null) {
            return new Object[0];
        }

        List list = new ArrayList();
        do {
            try {
                Field field = clazz.getDeclaredField(name);
                list.add(field.get(null));
            } catch (Throwable t) {
                ;
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        return list.toArray();
    }


    public static ClassLoader getExtClassLoader()
    {
        ClassLoader bootstrap = Object.class.getClassLoader();
        ClassLoader system = ClassLoader.getSystemClassLoader();
        ClassLoader preCl = system;
        ClassLoader cl;
        // bootstrapが非nullである可能性も考えているが（通常はnullなので）、
        // 意味があるか？
        while ((cl = preCl.getParent()) != null && cl != bootstrap) {
            preCl = cl;
        }
        if (preCl == system) {
            // 全く辿ることなくparentがnullの場合。この場合、
            // SurefireのIsolatedClassLoaderのように、parent==null
            // にしているClassLoaderである可能性があるので
            // ExtClassLoaderとみなさない方が良い。
            // こういう場合は仕方がないのでブートストラップクラスローダを
            // 返す。
            return bootstrap;
        }
        return preCl;
    }
}
