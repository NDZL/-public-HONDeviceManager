package com.honeywell.devicemanagersample;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NdzlWorker extends Worker {  //non può essere una inner class

    //private static final String TAG = NdzlWorker.class.getSimpleName();

    public NdzlWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();


        try {
            int a=0;
            return Result.success();

        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }
    }


    @Override
    public void onStopped() {
        super.onStopped();

    }
}
