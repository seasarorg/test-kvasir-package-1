package org.seasar.kvasir.base.log;

import org.seasar.kvasir.base.log.impl.ConsoleKvasirLog;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class KvasirLogFactory
{
    public static final String FACTORYCLASSNAME_DEFAULT = "org.seasar.kvasir.system.log.KvasirLogFactoryImpl";

    private static KvasirLogFactory factory_;

    private static final KvasirLog defaultLog_ = new ConsoleKvasirLog();


    abstract public KvasirLog getInstance(String name);


    abstract public KvasirLog getInstance(Class<?> clazz);


    abstract public long getWarnCount();


    abstract public long getErrorCount();


    abstract public long getFatalCount();


    public static KvasirLog getLog(String name)
    {
        if (factory_ != null) {
            return factory_.getInstance(name);
        } else {
            return defaultLog_;
        }
    }


    public static KvasirLog getLog(Class<?> clazz)
    {
        if (factory_ != null) {
            return factory_.getInstance(clazz);
        } else {
            return defaultLog_;
        }
    }


    public static KvasirLogFactory getFactory()
    {
        return factory_;
    }


    public void destroy()
    {
    }


    /*
     * for framework
     */

    public static void initializeFactory(ClassLoader classLoader)
    {
        initializeFactory(FACTORYCLASSNAME_DEFAULT, classLoader);
    }


    public static void initializeFactory(String factoryclassName,
        ClassLoader classLoader)
    {
        Thread thread = Thread.currentThread();
        ClassLoader old = thread.getContextClassLoader();
        try {
            // commons-loggingはコンテキストクラスローダからクラスを
            // ロードすること、またLog4jは最初のロガーオブジェクトの
            // 生成時にコンテキストクラスローダから設定ファイルを読み
            // 込むことからこうしている。
            thread.setContextClassLoader(classLoader);

            Class<?> clazz = Class.forName(factoryclassName, true, classLoader);
            factory_ = (KvasirLogFactory)clazz.newInstance();
            if (factory_ != null) {
                // Log4jは最初のLogインスタンスの生成時にコンテキスト
                // クラスローダから設定ファイルを読み込むので、
                // 指定されたクラスローダから設定ファイルが読まれるように
                // ここでLogインスタンスを生成しておく。
                factory_.getInstance("");
            }
        } catch (Throwable t) {
            ;
        } finally {
            thread.setContextClassLoader(old);
        }
    }


    public static void destroyFactory()
    {
        factory_.destroy();
        factory_ = null;
    }
}
