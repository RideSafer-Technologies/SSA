package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class Notification_Trigger extends IntentService {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean child;
    private boolean temp;
    private boolean charging;
    private boolean connection;
    private boolean alarmTrig;
    private int alarmType;

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
        if(child == true && charging == false) {
            while(child == true) {
                if(temp == true && alarmTrig == false) {
                    runAlarm();
                } else if (connection == false && alarmTrig == false) {
                    runAlarm();
                }
                updateData();
            }
            Toast.makeText(getApplicationContext(), " Your child was removed \n from the car seat", LENGTH_LONG).show();
            killAlarm();

        }

    }
    // Getter methods
    public boolean getChild(){
        return child;
    }
    public boolean getTemp(){
        return temp;
    }
    public boolean getCharging(){
        return charging;
    }
    //Setter methods
    public void setChild(boolean ch){
        child = ch;
    }
    public void setTemp(boolean tmp){
        temp = tmp;
    }
    public void setCharging(boolean chrg){
        charging = chrg;
    }
    public void setAlarmType(){
        // Set the type of alarm
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        alarmType = Integer.parseInt(sharedPreferences.getString("type_of_alarm_text", "0"));
    }
    // Not sure how to check for this yet. I think it will happen
    //when the communications class is done.
    public void setConnection(boolean con){
        connection = con;
    }
    // Alarm function checks for alarm type and then
    // executes an alarm.
    public void runAlarm(){
        if(alarmType == 0){
            Intent fullAlarm;
            fullAlarm = new Intent(getApplicationContext(), Full_Screen_Alarm.class);
            //The next two lines ensure there is only one instance of the fullScreen alarm
            //activity.
            fullAlarm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            fullAlarm.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(fullAlarm);
        }
        else if(alarmType == 1){
            //run code dialog alert
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
            startActivity(closeAlarm);
        }
        else if(alarmType == 1){
            //kill dialog alarm
        }
        else if(alarmType ==2){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        alarmTrig = false;
    }
    protected void updateData(){
        setChild(Data_Parser.getIsChildInSeat());
        setTemp(Data_Parser.getIsTempThresholdReached());
        setCharging(Data_Parser.getIsCharging());
        //setConnection(Communications.???insertGetFunction);
    }
    // Dialog Alert Code will be here


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
   /* private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
*/
    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    /*private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
    */
    
}
