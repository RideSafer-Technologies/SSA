package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ridesafertechnologies.ssa.util.SSA_Dialog_Alert;

import static android.widget.Toast.LENGTH_LONG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class Notification_Trigger extends IntentService {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static boolean child;
    private static boolean temp;
    private static boolean charging;
    private static boolean connection;
    private static boolean alarmTrig;
    private static int alarmType;

    public static void setDismissed(boolean dismissed) {
        Notification_Trigger.dismissed = dismissed;
    }

    private static boolean dismissed = false;

    private static final String TAG = "Notification Trigger: ";

    public Notification_Trigger() {
        super("Notification_Trigger");
        child = false;
        temp = false;
        charging = false;
        alarmType = 0;
        alarmTrig = false;
    }

    @Override
    protected void onHandleIntent(Intent Data) {
        updateData();
        setAlarmType();
        if(child && !alarmTrig && !dismissed) {
            Log.d(TAG, "Passed - if(child && !alarmTrig && !dismissed)");
                if(temp && !charging) {
                    Log.d(TAG, "Passed - if(temp && !charging)");
                    runAlarm();
                } else if (!connection) {
                    Log.d(TAG, "Failed - if(temp)");
                    Log.d(TAG, "Passed - else if (!connection)");
                    runAlarm();
                }
            if(!child && dismissed)
                Toast.makeText(this, " Your child was removed \n from the car seat", LENGTH_LONG).show();
        }
        if(alarmTrig && dismissed) {
            Log.d(TAG, "Killing Alarm");
            killAlarm();
        }
    }
    // Getter methods
    public static boolean getChild(){
        return child;
    }
    public static boolean getTemp(){
        return temp;
    }
    public static boolean getCharging(){
        return charging;
    }
    public static boolean getConnection() {
        return connection;
    }
    public static boolean getAlarmTrig() {
        return alarmTrig;
    }
    //Setter methods
    public static void setChild(boolean ch){
        child = ch;
    }
    public static void setTemp(boolean tmp){
        temp = tmp;
    }
    public static void setCharging(boolean chrg){
        charging = chrg;
    }
    public static void setAlarmTrig(boolean alarm) {
        alarmTrig = alarm;
    }
    public void setAlarmType(){
        // Set the type of alarm
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        alarmType = Integer.parseInt(sharedPreferences.getString("type_of_alarm_text", "0"));
    }
    // Not sure how to check for this yet. I think it will happen
    //when the communications class is done.
    public static void setConnection(boolean con){
        connection = con;
    }
    // Alarm function checks for alarm type and then
    // executes an alarm.
    public void runAlarm(){
        if(alarmType == 0){
            Log.d(TAG, "Full Screen Alarm: Run");
            alarmTrig = true;
            Intent fullAlarm;
            fullAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
            //The next two lines ensure there is only one instance of the fullScreen alarm
            //activity.
            fullAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fullAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fullAlarm.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(fullAlarm);
        }
        else if(alarmType == 1){
            //run code dialog alert
            Intent dialogAlert;
            dialogAlert = new Intent(getApplicationContext(), SSA_Dialog_Alert.class);
            dialogAlert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogAlert.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialogAlert.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(dialogAlert);

        }
        else if(alarmType == 2){
            //run code toast notification
            Toast.makeText(getApplicationContext(), "Stop Your Child is in the Car!", Toast.LENGTH_LONG).show();
            mediaPlayer = MediaPlayer.create(this,R.raw.tornadosireniidelilah747233690 );
            mediaPlayer.start();

        }
        else{
            //if the alarm type is an invalid setting, set to full screen.
            alarmType = 0;
            // then re-run the alarm
            runAlarm();
        }
        alarmTrig = true;
    }
    // The killAlarm method uses an intent to close the Full_Screen_Alarm
    protected void killAlarm(){
        if(alarmType ==0)
        {
            Intent closeAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
            closeAlarm.putExtra("close", true);
            closeAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(closeAlarm);
        }
        else if(alarmType == 1){
            //kill dialog alarm
            Intent closeDialog = new Intent(getApplicationContext(), SSA_Dialog_Alert.class);
            closeDialog.putExtra("close", true);
            startActivity(closeDialog);
        }
        else if(alarmType ==2){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        alarmTrig = false;
    }
    protected void updateData(){
        setChild(Data_Parser.getIsChildInSeat());
        Log.d(TAG, " Connection = " + connection);
        setTemp(Data_Parser.getIsTempThresholdReached());
        setCharging(Data_Parser.getIsCharging());
        setConnection(Communications.isConnectionStatus());
    }
    // Dialog Alert Code will be here

}
