package org.seasar.kvasir.base.timer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.timer.Job;
import org.seasar.kvasir.base.timer.TimerPlugin;
import org.seasar.kvasir.base.timer.extension.ExecutionFrequency;
import org.seasar.kvasir.base.timer.extension.JobElement;
import org.seasar.kvasir.base.timer.setting.TimerPluginSettings;


public class TimerPluginImpl extends AbstractPlugin<TimerPluginSettings>
    implements TimerPlugin
{
    private List<Job> onceJobs_ = new ArrayList<Job>();

    private List<Job> perMinuteJobs_ = new ArrayList<Job>();

    private ExecutorService executorService_;

    private static final KvasirLog log_ = KvasirLogFactory
        .getLog(TimerPluginImpl.class);


    @Override
    public Class<TimerPluginSettings> getSettingsClass()
    {
        return TimerPluginSettings.class;
    }


    protected boolean doStart()
    {
        for (JobElement element : getExtensionElements(JobElement.class)) {
            ExecutionFrequency enm = element.getExecutionFrequencyEnum();
            if (enm == ExecutionFrequency.ONCE) {
                onceJobs_.add((Job)element.getComponent());
            } else if (enm == ExecutionFrequency.PER_MINUTE) {
                perMinuteJobs_.add((Job)element.getComponent());
            } else {
                throw new IllegalArgumentException(
                    "Unknown execution-frequency: "
                        + element.getExecutionFrequency());
            }
        }

        int size = onceJobs_.size();
        if (!perMinuteJobs_.isEmpty()) {
            size++;
        }

        if (size > 0) {
            executorService_ = Executors.newFixedThreadPool(size);
        }

        return true;
    }


    @Override
    public void notifyKvasirStarted()
    {
        if (executorService_ != null) {
            log_.info("Starting job executor service...");
            if (!perMinuteJobs_.isEmpty()) {
                executorService_.execute(new JobRunner(perMinuteJobs_,
                    getSettings().getThreadPoolSize()));
            }
            for (Job job : onceJobs_) {
                executorService_.execute(job);
            }
            log_.info("Job executor service has been started.");
        } else {
            log_.info("No jobs are registered.");
        }
    }


    protected void doStop()
    {
        if (executorService_ != null) {
            log_.info("Shutting down job executor service...");
            executorService_.shutdown();
            log_.info("Job executor service has been shut down.");
            executorService_ = null;
        }

        onceJobs_.clear();
        perMinuteJobs_.clear();
    }
}
