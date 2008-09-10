package org.seasar.kvasir.base.webapp.multibyte;

import org.seasar.kvasir.base.plugin.Plugin;

import org.seasar.kvasir.base.webapp.multibyte.setting.MultibytePluginSettings;


public interface MultibytePlugin extends Plugin<MultibytePluginSettings>
{
    String ID = "org.seasar.kvasir.base.webapp.multibyte";
    String ID_PATH = ID.replace('.', '/');
}
