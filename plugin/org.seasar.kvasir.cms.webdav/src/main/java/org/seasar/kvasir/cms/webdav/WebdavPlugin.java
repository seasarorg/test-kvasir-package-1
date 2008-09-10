package org.seasar.kvasir.cms.webdav;

import javax.naming.directory.DirContext;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.webdav.setting.WebdavPluginSettings;


public interface WebdavPlugin
    extends Plugin<WebdavPluginSettings>
{
    String ID = "org.seasar.kvasir.cms.webdav";

    String ID_PATH = ID.replace('.', '/');


    boolean isWebdavEnabled();


    DirContext getDirContext();
}
