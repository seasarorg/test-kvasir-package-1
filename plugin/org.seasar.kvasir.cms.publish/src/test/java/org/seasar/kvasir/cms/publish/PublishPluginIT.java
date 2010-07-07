package org.seasar.kvasir.cms.publish;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class PublishPluginIT extends KvasirPluginTestCase<PublishPlugin>
{
    protected String getTargetPluginId()
    {
        return PublishPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        // kvasir-webappに直接的にも間接的にも依存していないプラグインの場合は
        // kvasir-webapp関連のクラスがないというエラーが発生することがあります。
        // その場合は第二引数をfalseにして下さい。
        return createTestSuite(PublishPluginIT.class, true);
    }
}
