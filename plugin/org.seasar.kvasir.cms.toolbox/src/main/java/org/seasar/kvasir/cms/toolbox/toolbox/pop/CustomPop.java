package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.kvasir.cms.java.CompileException;
import org.seasar.kvasir.cms.java.JavaPlugin;
import org.seasar.kvasir.cms.java.util.CompilerUtils;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.Page;


/**
 * 任意のロジックとテンプレートを指定できるPOPです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class CustomPop extends GenericPop
{
    public static final String ID = ToolboxPlugin.ID + ".customPop";

    public static final String PROP_LOGIC = "logic";

    public static final String VAR_EXCEPTION = "exception";

    private static final String SP = System.getProperty("line.separator");

    private static final String LOGIC_DEFAULT = "import java.util.Map;"
        + SP
        + "import org.seasar.kvasir.cms.pop.PopContext;"
        + SP
        + ""
        + SP
        + "public void process(PopContext context, String[] args, Map popScope)"
        + SP + "{" + SP + "}" + SP;

    private JavaPlugin javaPlugin_;

    private PopLogic popLogic_;

    private static final Log log = LogFactory.getLog(CustomPop.class);

    private static final String CLASSNAMESUFFIX = "PopLogic";

    private static final String LOCALPATHNAMESUFFIX = "/custom";


    public final void setJavaPlugin(JavaPlugin javaPlugin)
    {
        javaPlugin_ = javaPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        try {
            process(context, args, popScope);
        } catch (Throwable t) {
            popScope.put(VAR_EXCEPTION, t);
        }
        return super.render(context, args, popScope);
    }


    public void process(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        try {
            getPopLogicInstance(context).process(context, args, popScope);
        } catch (Throwable t) {
            log.error("Can't process logic of custom pop", t);
            popScope.put(VAR_EXCEPTION, t);
        }
    }


    @Override
    public final String getProperty(PopContext context, String id,
        String variant)
    {
        String value = super.getProperty(context, id, variant);
        if (value == null && PROP_LOGIC.equals(id)) {
            value = LOGIC_DEFAULT;
        }
        return value;
    }


    @Override
    public final String getProperty(PopContext context, String id, Locale locale)
    {
        String value = super.getProperty(context, id, locale);
        if (value == null && PROP_LOGIC.equals(id)) {
            value = LOGIC_DEFAULT;
        }
        return value;
    }


    @Override
    public final void setProperty(PopContext context, String id,
        String variant, String value)
    {
        if (PROP_LOGIC.equals(id)) {
            synchronized (this) {
                super.setProperty(context, id, variant, value);
                popLogic_ = null;
            }
        } else {
            super.setProperty(context, id, variant, value);
        }
    }


    synchronized PopLogic getPopLogicInstance(PopContext context)
        throws InstantiationException, IllegalAccessException, CompileException
    {
        if (popLogic_ == null) {
            popLogic_ = CompilerUtils.compile(javaPlugin_,
                LOCALPATHNAMESUFFIX + getInstanceId(), CLASSNAMESUFFIX,
                AbstractPopLogic.class,
                getProperty(context, PROP_LOGIC, Page.VARIANT_DEFAULT))
                .newInstance();
        }
        return popLogic_;
    }


    abstract public static class AbstractPopLogic
        implements PopLogic
    {
    }
}
