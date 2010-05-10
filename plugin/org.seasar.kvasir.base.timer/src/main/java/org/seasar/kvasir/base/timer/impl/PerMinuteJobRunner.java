package org.seasar.kvasir.base.timer.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.timer.Job;


/**
 * Jobを実行するクラスです。
 * 
 * @author skirnir
 */
public class PerMinuteJobRunner
    implements Runnable
{
    private final SimpleDateFormat sdf_ = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm");

    private static final KvasirLog log_ = KvasirLogFactory
        .getLog(PerMinuteJobRunner.class);

    private List<Job> jobs_;

    private Integer threadPoolSize_;

    private String runDate_;

    private ExecutorService executorService_;


    public PerMinuteJobRunner(List<Job> jobs, Integer threadPoolSize)
    {
        jobs_ = jobs;
        threadPoolSize_ = threadPoolSize;
    }


    public void run()
    {
        if (threadPoolSize_ > 0) {
            executorService_ = Executors.newFixedThreadPool(threadPoolSize_);
        } else {
            executorService_ = Executors.newCachedThreadPool();
        }

        // 開始時刻をなるべく00秒にあわせる。

        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        if (second > 0) {
            try {
                TimeUnit.SECONDS.sleep(60 - second);
            } catch (InterruptedException ex) {
                if (log_.isDebugEnabled()) {
                    log_.debug("Interrupted", ex);
                }
                return;
            }
        }

        try {
            while (true) {
                String date = sdf_.format(new Date());
                if (date.equals(runDate_)) {
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException ex) {
                        if (log_.isDebugEnabled()) {
                            log_.debug("Interrupted", ex);
                        }
                        return;
                    }
                    continue;
                }
                runDate_ = date;

                for (Job job : jobs_) {
                    executorService_.execute(job);
                }
            }
        } finally {
            executorService_.shutdown();
        }
    }
}
