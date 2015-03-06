package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.content.Intent;

import com.ridesafertechnologies.ssa.util.SSA_Dialog_Alert;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Snooze_Timer extends IntentService {
    private static boolean alarmtype;


    public Snooze_Timer() {
        super("Snooze_Timer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            alarmtype = intent.getExtras().getBoolean("alarmtype");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (alarmtype==true) {
                //fullscreen alarm snooze
                Intent resumeAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
                resumeAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resumeAlarm.putExtra("close", false);
                startActivity(resumeAlarm);
            } else if (alarmtype==false) {
                //dialog alarm snooze
                Intent resumeDialog = new Intent(getApplicationContext(), SSA_Dialog_Alert.class);
                resumeDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resumeDialog.putExtra("close", false);
                startActivity(resumeDialog);
            }
        }
    }

}
