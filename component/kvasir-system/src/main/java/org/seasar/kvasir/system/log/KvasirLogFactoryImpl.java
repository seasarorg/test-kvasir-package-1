package org.seasar.kvasir.system.log;

import org.apache.commons.logging.LogFactory;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;


/**
 * <p>
 * <b>同期化： </b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirLogFactoryImpl extends KvasirLogFactory
{
    private LogFactory factory_;

    private volatile long warnCount_;

    private volatile long errorCount_;

    private volatile long fatalCount_;


    public KvasirLogFactoryImpl()
    {
        factory_ = LogFactory.getFactory();
    }


    @Override
    public KvasirLog getInstance(String name)
    {
        return new KvasirLogImpl(this, factory_.getInstance(name));
    }


    @Override
    public KvasirLog getInstance(Class<?> clazz)
    {
        return new KvasirLogImpl(this, factory_.getInstance(clazz));
    }


    @Override
    public void destroy()
    {
        factory_.release();
        factory_ = null;
    }


    @Override
    public long getWarnCount()
    {
        return warnCount_;
    }


    void incrementWarnCount()
    {
        warnCount_++;
    }


    @Override
    public long getErrorCount()
    {
        return errorCount_;
    }


    void incrementErrorCount()
    {
        errorCount_++;
    }


    @Override
    public long getFatalCount()
    {
        return fatalCount_;
    }


    void incrementFatalCount()
    {
        fatalCount_++;
    }
}
