package com.ridesafertechnologies.ssa.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ridesafertechnologies.ssa.Data_Parser;

public class dataService extends Service {

    private static final int TIMED_DELAY_QUARTER = 250;
    private static final int TIMED_DELAY_HALF = 500;
    private static final String TAG = "dataService";
    public static final String DATA_SERVICE_ACTION = "com.ridesafertechnologies.ssa.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    Intent parserIntent;

    public dataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(DATA_SERVICE_ACTION);
        parserIntent = new Intent(this, Data_Parser.class);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        startService(parserIntent);
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, TIMED_DELAY_QUARTER);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, TIMED_DELAY_HALF);
        }
    };

    private void DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo");

        intent.putExtra("ChildInSeat", Data_Parser.getIsChildInSeat());
        intent.putExtra("TemperatureThreshold", Data_Parser.getIsTempThresholdReached());
        intent.putExtra("AlarmState", Data_Parser.getIsAlarmState());
        intent.putExtra("Charging", Data_Parser.getIsCharging());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }
}
