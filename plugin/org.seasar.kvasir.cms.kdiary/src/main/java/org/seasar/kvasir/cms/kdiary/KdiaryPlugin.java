package org.seasar.kvasir.cms.kdiary;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


public interface KdiaryPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.cms.kdiary";

    String ID_PATH = ID.replace('.', '/');
}
