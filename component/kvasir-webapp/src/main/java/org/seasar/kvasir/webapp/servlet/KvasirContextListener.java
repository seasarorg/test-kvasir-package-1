package org.seasar.kvasir.webapp.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.FileUtils;
import org.seasar.kvasir.webapp.Globals;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirContextListener
    implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent event)
    {
        ServletContext ctx = event.getServletContext();
        String homeDirectoryPath = FileUtils.toAbstractPath(ctx
            .getRealPath(Globals.KVASIR_HOME_PATH));
        MapProperties prop = new MapProperties();
        prop.setProperty(org.seasar.kvasir.base.Globals.PROP_SYSTEM_HOME_DIR,
            homeDirectoryPath);

        ComponentContainerFactory.setApplication(new ThreadLocalServletContext(
            ctx));

        Asgard
            .establish("kvasir.xproperties", "custom.xproperties", prop, null);
    }


    public void contextDestroyed(ServletContextEvent event)
    {
        Asgard.ragnarok(10);
    }
}
