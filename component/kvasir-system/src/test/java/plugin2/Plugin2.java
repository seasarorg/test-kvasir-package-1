package plugin2;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;


public class Plugin2 extends AbstractPlugin<EmptySettings>
{
    @Override
    protected boolean doStart()
    {
        return false;
    }


    @Override
    protected void doStop()
    {
    }
}
