package org.seasar.kvasir.base.container;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class ComponentContainerFactory
{
    private static ComponentContainerFactory instance_;

    private static Object application_;


    public static Object getApplication()
    {
        return application_;
    }


    public static void setApplication(Object application)
    {
        application_ = application;
    }


    public static void initialize(String factoryClassName, ClassLoader cl)
    {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            try {
                Class<?> clazz = Class.forName(factoryClassName, true, cl);
                instance_ = (ComponentContainerFactory)clazz.newInstance();
            } catch (Throwable t) {
                throw new RuntimeException(
                    "Can't create ComponentContainerFactory instance: factory class name is '"
                        + factoryClassName + "'", t);
            }
            instance_.prepare();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }

        if (application_ != null) {
            instance_.getRootContainer().setApplication(application_);
        }
    }


    public static ComponentContainerFactory getInstance()
    {
        return instance_;
    }


    public static void destroy()
    {
        application_ = null;
        if (instance_ != null) {
            try {
                instance_.stop();
            } finally {
                instance_ = null;
            }
        }
    }


    protected void prepare()
    {
    }


    public void freeze()
    {
    }


    abstract public ComponentContainer getRootContainer();


    public ComponentContainer createContainer(String configPath, ClassLoader cl)
    {
        return createContainer(configPath, cl, new ComponentContainer[0]);
    }


    /**
     * コンポーネントコンテナオブジェクトを生成して返します。
     * <p><code>configResourcePath</code>で指定されたコンポーネント定義を使って
     * コンポーネントコンテナオブジェクトを生成します。</p>
     * <p><code>configResourcePath</code>としてnullが指定された場合は
     * 空のコンポーネントコンテナオブジェクトを生成します。</p>
     * <p><code>requirements</code>
     * で依存するコンポーネントコンテナを指定することもできます。
     * <code>requirements</code>として空でない配列が指定された場合、
     * 生成したコンポーネントコンテナから
     * 配列に含まれる全てのコンポーネントコンテナが持つコンポーネントを
     * 利用可能にします。</p>
     *
     * @param configPath コンポーネント定義Javaリソースのパス。
     * nullを指定することもできます。
     * @param cl コンポーネントのクラスを読み込むためのクラスローダ。
     * nullを指定することもできます。
     * @param requirements 依存するコンポーネントコンテナの配列。
     * nullや空の配列を指定することもできます。
     * @return コンポーネントコンテナ。nullが返されることはありません。
     */
    abstract public ComponentContainer createContainer(String configPath,
        ClassLoader cl, ComponentContainer[] requirements);


    protected void stop()
    {
    }


    public void beginSession()
    {
    }


    public void endSession()
    {
    }


    /**
     * 現在のスレッドに関連付けるコンテキストクラスローダとして使用すべきクラスローダを返します。
     * <p>このメソッドは基本的には単にコンテキストクラスローダを返しますが、
     * より適切なクラスローダを返すようにサブクラスでこのメソッドがオーバライドされることがあります。
     * </p>
     * 
     * @return クラスローダ。
     */
    public ClassLoader getCurrentClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }
}
