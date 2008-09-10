package org.seasar.kvasir.page.ability;

import java.util.Iterator;

import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;


/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public abstract class AbstractPageAbilityAlfr
    implements PageAbilityAlfr, Lifecycle
{
    private PagePlugin pagePlugin_;

    private boolean started_ = false;

    private PageAlfr pageAlfr_;

    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    abstract protected boolean doStart();


    abstract protected void doStop();


    public final boolean start()
    {
        if (started_) {
            return true;
        }

        pageAlfr_ = pagePlugin_.getPageAlfr();

        boolean started;
        try {
            started = doStart();
        } catch (Throwable t) {
            log_.error("Can't start PageAbilityAlfr: " + getClass(), t);
            started = false;
        }
        started_ = started;
        return started;
    }


    public final void stop()
    {
        if (!started_) {
            return;
        }

        try {
            doStop();
        } catch (Throwable t) {
            log_.error("Can't stop PageAbilityAlfr gracefully: " + getClass(),
                t);
        }

        pagePlugin_ = null;

        pageAlfr_ = null;

        started_ = false;
    }


    public final boolean isStarted()
    {
        return started_;
    }


    public Iterator<String> attributeNames(Page page, String variant)
    {
        return attributeNames(page, variant, new AttributeFilter());
    }


    /*
     * protected scope methods
     */

    protected PagePlugin getPagePlugin()
    {
        return pagePlugin_;
    }


    protected PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    /*
     * for framework
     */

    // サブクラスのことを考えて、メソッド名はsetPlugin()にしていない。
    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
