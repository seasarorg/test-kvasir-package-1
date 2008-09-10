package org.seasar.kvasir.base.mime.impl;

import org.seasar.kvasir.base.mime.MimeMappings;
import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.base.mime.extension.MimeMappingElement;
import org.seasar.kvasir.base.mime.setting.MimePluginSettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;


public class MimePluginImpl extends AbstractPlugin<MimePluginSettings>
    implements MimePlugin
{
    private MimeMappings mimeMappings_;


    protected boolean doStart()
    {
        mimeMappings_ = new MimeMappingsImpl(
            getExtensionElements(MimeMappingElement.class));

        return true;
    }


    protected void doStop()
    {
        mimeMappings_ = null;
    }


    public MimeMappings getMimeMappings()
    {
        return mimeMappings_;
    }

}
