package com.ridesafertechnologies.ssa.util;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ridesafertechnologies.ssa.Communications;
import com.ridesafertechnologies.ssa.Notification_Trigger;

public class demoMode extends Service {
    /*   "#Force+Temperature+Alarm+Charging+~"   */
    // These final Strings hold the Strings used for the demo purposes
    private static final String DEMO_STRING_0 = "#0+70+0+0+~";
    private static final String DEMO_STRING_1 = "#500+70+0+0+~";
    private static final String DEMO_STRING_2 = "#500+95+1+0+~";
    private static final String DEMO_STRING_3 = "#0+70+0+1+~";
    private static final String DEMO_STRING_4 = "#500+70+0+1+~";
    private static final String DEMO_STRING_5 = "#500+40+1+1+~";
    private static final String DEMO_STRING_6 = "#500+70+0+0+~";

    // Value for strings
    private static final int DEMO_STRING_0_VAL = 0;
    private static final int DEMO_STRING_1_VAL = 1;
    private static final int DEMO_STRING_2_VAL = 2;
    private static final int DEMO_STRING_3_VAL = 3;
    private static final int DEMO_STRING_4_VAL = 4;
    private static final int DEMO_STRING_5_VAL = 5;
    private static final int DEMO_STRING_6_VAL = 6;
    private static final int DEMO_STRING_FINISH = -1;

    // TAG for service
    private static final String TAG = "demoMode: ";

    // These integers will hold the timing delays for the demo purposes
    private static final int TIMER_NONE = 0;
    private static final int TIMER_5_SEC = 5000;
    private static final int TIMER_10_SEC = 10000;
    private static final int TIMER_20_SEC = 20000;
    private static final int TIMER_30_SEC = 30000;

    // Index ID
    private static int id = 0;

    // Intent to run demo
    Intent demoIntent;
    public static final String DEMO_SERVICE_ACTION = "com.ridesafertechnologies.ssa.demoevent";

    // Handler for demo
    private final Handler handler = new Handler();

    public demoMode() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        demoIntent = new Intent(DEMO_SERVICE_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(delay);
        handler.postDelayed(delay, TIMER_NONE);
    }

    private Runnable delay = new Runnable() {
        public void run() {

            Log.d(TAG, "Start Runnable: id = " + id +
                    "\nAlarm Trig : " + Notification_Trigger.getAlarmTrig() +
                    "\nCharging   : " + Notification_Trigger.getCharging() +
                    "\nChild      : " + Notification_Trigger.getChild() +
                    "\nConnection : " + Notification_Trigger.getConnection() +
                    "\nTemperature: " + Notification_Trigger.getTemp());
            int theDelay;
            if (id == DEMO_STRING_FINISH)
                return;
            pushString(id);


            switch (id) {
                case DEMO_STRING_FINISH:
                    id = DEMO_STRING_FINISH;
                    theDelay = TIMER_NONE;
                    break;
                case DEMO_STRING_0_VAL:
                    id = DEMO_STRING_1_VAL;
                    theDelay = TIMER_10_SEC;
                    Communications.setConnectionStatus(true);
                    break;
                case DEMO_STRING_1_VAL:
                    id = DEMO_STRING_2_VAL;
                    theDelay = TIMER_20_SEC;
                    break;
                case DEMO_STRING_2_VAL:
                    id = DEMO_STRING_3_VAL;
                    theDelay = TIMER_20_SEC;
                   // Notification_Trigger.demoOverride(false);
                    break;
                case DEMO_STRING_3_VAL:
                    id = DEMO_STRING_4_VAL;
                    theDelay = TIMER_20_SEC;
                    break;
                case DEMO_STRING_4_VAL:
                    id = DEMO_STRING_5_VAL;
                    theDelay = TIMER_10_SEC + TIMER_5_SEC;
                    break;
                case DEMO_STRING_5_VAL:
                    id = DEMO_STRING_6_VAL;
                    theDelay = TIMER_30_SEC + TIMER_5_SEC;
                    break;
                case DEMO_STRING_6_VAL:
                    id = DEMO_STRING_FINISH;
                    theDelay = TIMER_30_SEC;
                    Notification_Trigger.setAlarmTrig(false);
                    Communications.setConnectionStatus(false);
                    Notification_Trigger.setDismissed(false);
                    break;
                default:
                    id = DEMO_STRING_FINISH;
                    theDelay = TIMER_NONE;
                    break;
            }

            handler.postDelayed(this, theDelay);
        }
    };

    private void pushString(int inID) {
        String token;

        switch (inID) {
            case DEMO_STRING_FINISH:
                token = DEMO_STRING_0;
                break;
            case DEMO_STRING_0_VAL:
                token = DEMO_STRING_0;
                break;
            case DEMO_STRING_1_VAL:
                token = DEMO_STRING_1;
                break;
            case DEMO_STRING_2_VAL:
                token = DEMO_STRING_2;
                break;
            case DEMO_STRING_3_VAL:
                token = DEMO_STRING_3;
                break;
            case DEMO_STRING_4_VAL:
                token = DEMO_STRING_4;
                break;
            case DEMO_STRING_5_VAL:
                token = DEMO_STRING_5;
                break;
            case DEMO_STRING_6_VAL:
                token = DEMO_STRING_6;
                break;
            default:
                token = DEMO_STRING_0;
                break;
        }

        Communications.setDataToken(token);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(delay);
        super.onDestroy();
    }
}
