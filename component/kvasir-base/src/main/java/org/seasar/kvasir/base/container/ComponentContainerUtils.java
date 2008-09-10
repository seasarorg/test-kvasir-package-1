package org.seasar.kvasir.base.container;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ComponentContainerUtils
{
    private ComponentContainerUtils()
    {
    }


    public static Object getComponent(ComponentContainer container,
        String name, String className, ClassLoader classLoader)
    {
        ComponentNotFoundRuntimeException cnfrex = null;

        if (container != null) {
            Object key;
            if (name != null) {
                key = name;
            } else if (className != null) {
                try {
                    key = Class.forName(className, true, classLoader);
                } catch (ClassNotFoundException ex) {
                    throw (IllegalArgumentException)new IllegalArgumentException(
                        "Can't resolve class: " + className).initCause(ex);
                }
            } else {
                return null;
            }
            try {
                return container.getComponent(key);
            } catch (ComponentNotFoundRuntimeException ex) {
                // コンポーネントが見つからなかった場合は直接生成する。
                cnfrex = ex;
            }
        }

        // ComponentContainerがない場合はClass.forName().newInstance()
        // する。
        if (className == null) {
            if (cnfrex != null) {
                throw cnfrex;
            } else {
                return null;
            }
        }
        try {
            Class<?> clazz = Class.forName(className, true, classLoader);
            if (clazz.isInterface() && (cnfrex != null)) {
                // コンテナにコンポーネントが見つからなかったため
                // 直接コンポーネントを生成しようとしたが、対象クラスが
                // インタフェースだった。
                // →コンポーネント定義ファイルの設定ミスの可能性が高い。
                throw cnfrex;
            }
            return clazz.newInstance();
        } catch (ClassNotFoundException ex) {
            throw (IllegalArgumentException)new IllegalArgumentException(
                "Can't resolve class: " + className + " in classLoader("
                    + classLoader + ")").initCause(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Can't access constructor: "
                + className);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("Can't instanciate: "
                + className);
        }
    }


    public static Object findComponent(ComponentContainer container,
        String baseName, String key)
    {
        if (container == null) {
            return null;
        }

        if (key != null) {
            try {
                return container.getComponent(baseName + "_" + key);
            } catch (ComponentNotFoundRuntimeException ex) {
                ;
            }
        }
        return container.getComponent(baseName);
    }
}
