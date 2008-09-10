package org.seasar.kvasir.base.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Child;


/**
 * @author manhole
 */
public class PluginAlfrSettings
{

    /**
     * Maven2リモートレポジトリのパスです。
     */
    private List<RemoteRepository> remoteRepositories = new ArrayList<RemoteRepository>();


    public RemoteRepository[] getRemoteRepositories()
    {
        return remoteRepositories.toArray(new RemoteRepository[0]);
    }


    public void setRemoteRepositories(
        final RemoteRepository[] remoteRepositories)
    {
        this.remoteRepositories = Arrays.asList(remoteRepositories);
    }


    @Child
    public void addRemoteRepository(final RemoteRepository remoteRepository)
    {
        remoteRepositories.add(remoteRepository);
    }

}
