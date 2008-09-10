package org.seasar.kvasir.base.mime;

import org.seasar.kvasir.base.mime.setting.MimePluginSettings;
import org.seasar.kvasir.base.plugin.Plugin;


public interface MimePlugin
    extends Plugin<MimePluginSettings>
{
    String ID = "org.seasar.kvasir.base.mime";

    String ID_PATH = ID.replace('.', '/');


    MimeMappings getMimeMappings();
}
