package com.hardwork.fg607.relaxfinger.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FloatJobService extends JobService {

    private static final int JOB_ID = 1100;

    public FloatJobService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        if(FloatService.instance == null){

            Intent intent = new Intent();
            intent.setClass(this, FloatService.class);
            startService(intent);
        }


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleService(Context context) {
        JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context.getPackageName(), FloatJobService.class.getName()));
        builder.setPersisted(true);
        builder.setPeriodic(3 * 1000);
        js.cancel(JOB_ID);
        js.schedule(builder.build());
    }
}
