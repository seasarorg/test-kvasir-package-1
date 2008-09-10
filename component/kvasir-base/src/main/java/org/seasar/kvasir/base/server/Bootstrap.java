package org.seasar.kvasir.base.server;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.FileUtils;


public class Bootstrap
{
    private static final int TIMEOUT_SECONDS = 10;


    public static void main(String[] args)
    {
        String homeDirectoryPath = FileUtils.toAbstractPath(System
            .getProperty(Globals.PROP_SYSTEM_HOME_DIR));
        if (homeDirectoryPath == null) {
            System.out.println("Specify system property '"
                + Globals.PROP_SYSTEM_HOME_DIR + "'.");
            System.exit(1);
            return;
        }
        System.out.println(Globals.PROP_SYSTEM_HOME_DIR + ": "
            + homeDirectoryPath);

        MapProperties prop = new MapProperties();
        prop.setProperty(Globals.PROP_SYSTEM_HOME_DIR, homeDirectoryPath);

        if (!Asgard.establish("kvasir.xproperties", "custom.xproperties", prop,
            null)) {
            System.out.println("Failed to start Kvasir/Sora.");
            System.exit(1);
            return;
        }
        System.out.println("Kvasir/Sora started.");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run()
            {
                ragnarok(TIMEOUT_SECONDS);
            }
        }));

        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
        } finally {
            ragnarok(TIMEOUT_SECONDS);
        }
    }


    static void ragnarok(int timeoutSeconds)
    {
        try {
            Asgard.ragnarok(timeoutSeconds);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("Kvasir/Sora stopped.");
    }
}
