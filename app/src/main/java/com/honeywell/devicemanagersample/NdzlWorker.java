package com.honeywell.devicemanagersample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            phoneHome();
            return Result.success();

        } catch (Throwable e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    void phoneHome(){

    }




    String getAndroidAPI(){
        String _sb_who =  Build.MANUFACTURER+","+ Build.MODEL+"\n"+ Build.DISPLAY+", API:"+ android.os.Build.VERSION.SDK_INT;
        return  _sb_who;
    }


    @Override
    public void onStopped() {
        super.onStopped();

    }
}
