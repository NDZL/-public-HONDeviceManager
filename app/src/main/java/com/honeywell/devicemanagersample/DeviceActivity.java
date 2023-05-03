package com.honeywell.devicemanagersample;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.honeywell.osservice.data.OSConstant;
import com.honeywell.osservice.sdk.DeviceManager;
import com.honeywell.osservice.sdk.HonOSException;
import com.honeywell.osservice.sdk.CreateListener;

import com.honeywell.osservice.sdk.BatteryManager;

import com.honeywell.osservice.sdk.OSSDKCallback;
import com.honeywell.osservice.sdk.SystemConfigManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DeviceActivity extends AppCompatActivity {
    public DeviceManager mDeviceManager;
    private BatteryManager mBatteryManager;
    private SystemConfigManager mSystemConfigManager;

    private EditText etAssetNumber;
    private TextView textView;
    private Spinner sensor_spinner;
    private Spinner spinnerActiveTouchProfile;
    Switch switch1;
    Switch switch2;
    private Switch switch3;

    class OSSDKCallbackImpl implements OSSDKCallback
    {
        public String function_name="";
        @Override
        public void onResult(Bundle bundle) {
            if(bundle.get("status").toString().equals("ok"))
                textView.append("\n"+function_name +" call is success");
            else
                textView.append("\n"+function_name +" call is failed:"+bundle.get("message")+"\n");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        etAssetNumber = (EditText) findViewById(R.id.etAssetNumber);
        textView=(TextView) findViewById(R.id.textView);
        sensor_spinner = (Spinner) findViewById(R.id.sensor_spinner);
        spinnerActiveTouchProfile = (Spinner) findViewById(R.id.spinnerActiveTouchProfile);
        textView.setMovementMethod(new ScrollingMovementMethod());
        switch1=(Switch) findViewById(R.id.switch1);
        switch2=(Switch) findViewById(R.id.switch2);
        switch3=(Switch) findViewById(R.id.switch3);

        textView.append(getTargetSDK()+"\nAPI:"+getAndroidAPI()+"\n");

        DeviceManager.create(this, new CreateListener<DeviceManager>() {
            @Override
            public void onCreate(DeviceManager deviceManager) {

                mDeviceManager = deviceManager;
            }

            @Override
            public void onError(String s) {
                Log.e("OSSDK_Demo", "onError: DeviceManager:" + s);
            }
        });

        BatteryManager.create(this, new CreateListener<BatteryManager>() {
            @Override
            public void onCreate(BatteryManager batteryManager) {
                mBatteryManager = batteryManager;
            }

            @Override
            public void onError(String s) {
                Log.e("OSSDK_Demo", "onError: BatteryManager:" + s);
            }
        });

        SystemConfigManager.create(this, new CreateListener<SystemConfigManager>() {
            @Override
            public void onCreate(SystemConfigManager systemConfigManager) {
                mSystemConfigManager = systemConfigManager;
            }

            @Override
            public void onError(String s) {
                Log.e("OSSDK_Demo", "onError: SystemConfigManager:" + s);
            }
        });
    }
    public void setAssetNumber(View v) {
        if (null != mDeviceManager) {
            try {
                String assetNum = etAssetNumber.getText().toString();
                if (assetNum.isEmpty()) { //|| assetNum.length() != 15) {
                    Toast.makeText(this, "Asset Number length must be > 0", Toast.LENGTH_LONG).show();
                    return;
                }
                mDeviceManager.setAssetNumber(assetNum);
                textView.setText("Asset Number = "+assetNum+"\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.setText("Asset Number = "+e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void getHallSensorStatus(View v) {
        if (null != mDeviceManager) {
            int position = sensor_spinner.getSelectedItemPosition();
            String sensorID = null;
            switch (position) {
                case 0: // SIM
                    sensorID = OSConstant.KEY_RESULT_HALL_SENSOR_ID_SIM_STATUS;
                    break;
                case 1: // Scanner Trigger
                    sensorID = OSConstant.KEY_RESULT_HALL_SENSOR_ID_SCANNER_TRIGGER_STATUS;
                    break;
                case 2: // Dock Connected
                    sensorID = OSConstant.KEY_RESULT_HALL_SENSOR_ID_DOCK_CONNECTED_STATUS;
                    break;
                case 3: // all
                    sensorID = null;
                    break;
            }
            try {
                Bundle status = mDeviceManager.getHallSensorStatus(sensorID);
                for (String key:status.keySet()) {
                    boolean hInfo = status.getBoolean(key);
                textView.append(key + " = " + hInfo + "\n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("HallSensorStatus = " + e.getMessage() + "\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void setActiveTouchProfile(View v) {
        if (null != mDeviceManager) {
            try {
                int position = spinnerActiveTouchProfile.getSelectedItemPosition();
                String profile = null;
                switch (position) {
                    case 0: // Normal
                        profile = OSConstant.TouchProfile.NORMAL;
                        break;
                    case 1: // FINGER
                        profile = OSConstant.TouchProfile.FINGER;
                        break;
                    case 2: // GLOVE
                        profile = OSConstant.TouchProfile.GLOVE;
                        break;
                    case 3: // STYLUS
                        profile = OSConstant.TouchProfile.STYLUS;
                        break;
                }
                mDeviceManager.setActiveTouchProfile(profile);
                textView.append("Active Touch Profile = " + profile + "\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Active Touch Profile = " + e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public String getBatteryInfo() {
        String bout ="N/A";
        StringBuilder sb= new StringBuilder();
        if (null != mBatteryManager) {
            Bundle batteryInfo = null;
            try {
                batteryInfo = mBatteryManager.getBatteryGaugeInfo(null);
                for (String key:batteryInfo.keySet()) {
                    String bInfo = batteryInfo.getString(key);
                    Log.d("OSSDK_Demo",key+":"+bInfo+"\n");
                    sb.append (key+":"+bInfo+"\n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                bout=e.getMessage();
                e.printStackTrace();
            }

        }
        bout = sb.toString();
        return bout;
    }


    public void factoryReset(View v) {
/*        if (null != mDeviceManager) {
            try {
               // mDeviceManager.wipeDevice(OSConstant.RESET_TYPE_FACTORY);
                int a=0;
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                e.printStackTrace();
            }
        }*/
    }
    public void enterpriseReset(View v) {
        /*if (null != mDeviceManager) {
            try {
                //mDeviceManager.wipeDevice(OSConstant.RESET_TYPE_ENTERPRISE);
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                e.printStackTrace();
            }
        }*/
    }
    public void deviceInfo(View v){
        if(null != mDeviceManager){
            try {
                String assetNum = mDeviceManager.getAssetNumber();
                textView.append("Asset Number = " + assetNum+"\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Asset Number = "+e.getMessage()+"\n");
                e.printStackTrace();
            }
            try {
                String imei = mDeviceManager.getIMEI();
                textView.append("IMEI = " + imei+"\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("IMEI = " + e.getMessage()+"\n");
                e.printStackTrace();
            }
            try {
                String batt = getBatteryInfo();
                textView.append("BATTERY = "+batt+"\n");
                Log.d("OSSDK_Demo", "BATTERY= " + batt);
            } catch (Exception e) {
/*                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Active Touch Profile = "+e.getMessage()+"\n");
                e.printStackTrace();*/
            }
            try {
                String sn = mDeviceManager.getSerialNumber();
                textView.append(getTargetSDK()+"\nAPI:"+getAndroidAPI()+"\n");
                textView.append("Device Serial Number = " + sn+"\n\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Device Serial Number = " + e.getMessage()+"\n");
                e.printStackTrace();
            }
            try {
                String temperature = mDeviceManager.getInternalTemperature();
                textView.append("Internal Temperature = "+temperature+"\n");
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Internal Temperature = "+e.getMessage()+"\n");
                e.printStackTrace();
            }
            try {
                Bundle bluetoothAddress = mDeviceManager.getBluetoothAddress(null);
                for (String key:bluetoothAddress.keySet()) {
                    String addressInfo = bluetoothAddress.getString(key);
                    Log.d("OSSDK_Demo",key+" = "+addressInfo);
                    textView.append(key+" = "+addressInfo+"\n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Bluetooth Address = "+e.getMessage()+"\n");
                e.printStackTrace();
            }
            try {
                Bundle macAddress = mDeviceManager.getWifiMacAddress(null);
                for (String key:macAddress.keySet()) {
                    String addressInfo = macAddress.getString(key);
                    Log.d("OSSDK_Demo",key+" = "+addressInfo);
                    textView.append(key+" = "+addressInfo+"\n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("WifiMacAddress = "+e.getMessage()+"\n");
                //e.printextView.appendtStackTrace();
            }

            try{
                schedulePeriodicJob();
            }
            catch(Exception e){}

            //GET METHODS FROM SystemConfigManager
           // textView.append(getSDKVersion()+"\n");
            textView.append(getCounters()+"\n");


        }
    }
    public void clear(View v){
        textView.setText("");
    }
    public void navigate(View v){
        Intent intent = new Intent(v.getContext(), OtherFeaturesActivity.class);
        startActivity(intent);
    }
    public void keyInputStatus(View v){
        if (null != mDeviceManager) {
            try {
            //boolean status = mDeviceManager.getKeyInputStatus();
            if (switch1.isChecked()) {
                switch1.getTextOn().toString();
                mDeviceManager.enableKeyInput();
                textView.append("Key Input Enabled = "+ "True"+"\n");
            } else {
               switch1.getTextOff().toString();
                mDeviceManager.disableKeyInput();
                textView.append("Key Input Enabled = "+ "False"+"\n");
            }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Key Input = "+ e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void touchInputStatus(View v){
        if (null != mDeviceManager) {
            try {
               // boolean status = mDeviceManager.getTouchscreenStatus();
                if (switch2.isChecked()) {
                    switch2.getTextOn().toString();
                    mDeviceManager.enableTouchscreen();
                    textView.append("Touch Input Enabled = "+ "True"+"\n");
                } else {
                    switch2.getTextOff().toString();
                    mDeviceManager.disableTouchscreen();
                    textView.append("Touch Input Enabled = "+ "False"+"\n"+"Will enable touch input in 5 seconds");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                mDeviceManager.getTouchscreenStatus();
                            } catch (HonOSException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);


                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                mDeviceManager.enableTouchscreen();
                                switch2.getTextOn().toString();
                            } catch (HonOSException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 10000);

                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("Touch Input = "+ e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void USBStatus(View v){
        if (null != mDeviceManager) {
            try {
                if (switch3.isChecked()) {
                    switch3.getTextOn().toString();
                    mDeviceManager.enableUSBInput();
                    textView.append("USB Enabled \n");
                } else {
                    switch3.getTextOff().toString();
                    mDeviceManager.disableUSBInput();
                    textView.append("USB Disabled \n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("USBStatus="+ e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void getUSBStatus(View v)
    {
        if (null != mDeviceManager) {
            try {
                switch3.setChecked(mDeviceManager.getUSBInputStatus());
                if (switch3.isChecked()) {
                    textView.append("USB Enabled \n");
                } else {
                    textView.append("USB Disabled \n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("GetUSBStatus="+ e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void getKeyInputStatus(View v)
    {
        if (null != mDeviceManager) {
            try {
                textView.append("\nKey Input Status:"+mDeviceManager.getKeyInputStatus()+"\n");
                switch1.setChecked(mDeviceManager.getKeyInputStatus());
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("\nGetKeyInputStatus="+ e.getMessage()+"\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void getTouchProfiles(View v) {
        if (null != mDeviceManager) {
            try {
                String[] str = mDeviceManager.getTouchProfiles();
                for (String strVal : str) {
                    textView.append("\n" + strVal);
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                textView.append("\nGetTouchProfiles=" + e.getMessage() + "\n");
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }


    String getTargetSDK(){
        int version = 0;
        String app_username="";
        PackageManager pm = getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(getPackageName() , 0);
        } catch (PackageManager.NameNotFoundException e) {}
        if (applicationInfo != null) {
            version = applicationInfo.targetSdkVersion;
            app_username = AndroidFileSysInfo.getNameForId( applicationInfo.uid );
        }
        return  "APP_TARGET_API:"+version+" APP_USER:"+app_username;
    }

    String getAndroidAPI(){
        String _sb_who =  Build.MANUFACTURER+","+ Build.MODEL+"\n"+ Build.DISPLAY+", API:"+ android.os.Build.VERSION.SDK_INT;
        return  _sb_who;
    }

    public String getCounters(){

        StringBuilder sbout = new StringBuilder();

        if (null != mSystemConfigManager) {
            try {
                Bundle counters = mSystemConfigManager.getCounters(null);
                for(String key : counters.keySet()) {
                    Log.d("OSSDK_Demo", "counters: " + key + " " + counters.getInt(key));
                    sbout.append("Counters:"+ key + " "+counters.getInt(key)+"\n");
                }
            } catch (HonOSException e) {
                Log.e("OSSDK_Demo", e.getErrorCode() + " " + e.getMessage());
                e.printStackTrace();
            }
        }
        return sbout.toString();
    }

    //https://developer.android.com/reference/androidx/work/PeriodicWorkRequest?hl=en
    public void schedulePeriodicJob() {
        PeriodicWorkRequest pingWorkRequest =
                new PeriodicWorkRequest.Builder(NdzlWorker.class, 15, TimeUnit.MINUTES)
                        .addTag("PERIODIC_TASK")
                        //.setConstraints(anyNetworkConstraint)
                        .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork("uniqueWorkName",
                                   ExistingPeriodicWorkPolicy.REPLACE,
                                   pingWorkRequest);

        //mettere lettura flag pwrm.isIgnoringBatteryOptimizations(name)  nei log
    }



}
