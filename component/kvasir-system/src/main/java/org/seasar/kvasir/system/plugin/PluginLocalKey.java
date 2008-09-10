package org.seasar.kvasir.system.plugin;

public class PluginLocalKey
{
    private String pluginId_;

    private Object localKey_;


    public PluginLocalKey(String pluginId, Object localKey)
    {
        pluginId_ = pluginId;
        localKey_ = localKey;
    }


    @Override
    public String toString()
    {
        return "(" + pluginId_ + ", " + localKey_ + ")";
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
            + ((localKey_ == null) ? 0 : localKey_.hashCode());
        result = PRIME * result
            + ((pluginId_ == null) ? 0 : pluginId_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PluginLocalKey other = (PluginLocalKey)obj;
        if (localKey_ == null) {
            if (other.localKey_ != null)
                return false;
        } else if (!localKey_.equals(other.localKey_))
            return false;
        if (pluginId_ == null) {
            if (other.pluginId_ != null)
                return false;
        } else if (!pluginId_.equals(other.pluginId_))
            return false;
        return true;
    }


    public Object getLocalKey()
    {
        return localKey_;
    }


    public String getPluginId()
    {
        return pluginId_;
    }
}
