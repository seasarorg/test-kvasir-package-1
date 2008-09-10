package org.seasar.kvasir.base.plugin;

public class SettingsEvent<S>
{
    private S oldSettings_;

    private S newSettings_;


    public SettingsEvent(S oldSettings, S newSettings)
    {
        oldSettings_ = oldSettings;
        newSettings_ = newSettings;
    }


    public S getNewSettings()
    {
        return newSettings_;
    }


    public S getOldSettings()
    {
        return oldSettings_;
    }
}
