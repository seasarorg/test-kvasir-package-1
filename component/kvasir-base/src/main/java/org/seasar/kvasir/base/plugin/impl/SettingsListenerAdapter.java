package org.seasar.kvasir.base.plugin.impl;

import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.SettingsListener;


abstract public class SettingsListenerAdapter<S>
    implements SettingsListener<S>
{
    public void notifyUpdated(SettingsEvent<S> event)
    {
    }
}
