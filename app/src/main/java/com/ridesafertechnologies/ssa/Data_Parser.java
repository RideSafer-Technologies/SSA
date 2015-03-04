package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 *
 */
public class Data_Parser extends IntentService {

    //-------------------------------------FINAL VARIABLES----------------------------------------//
    private static final int FORCE_INDEX = 0;
    private static final int TEMP_INDEX = 1;
    private static final int ALARM_INDEX = 2;
    private static final int CHARGING_INDEX = 3;
    private static final int STRING_PARTS_SIZE = 4;


    //----------------------------------System Wide Variables-------------------------------------//
    private static boolean isChildInSeat = false;
    private static boolean isTempThresholdReached = false;
    private static boolean isAlarmState = false;
    private static boolean isCharging = false;


    //-----------------------------------------Getters--------------------------------------------//
    public static boolean getIsChildInSeat() {
        return isChildInSeat;
    }

    public static boolean getIsTempThresholdReached() {
        return isTempThresholdReached;
    }

    public static boolean getIsAlarmState() { return isAlarmState; }

    public static boolean getIsCharging() {
        return isCharging;
    }

    public Data_Parser() {
        super("Data_Parser");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String communicationsData = Communications.getDataToken();

        if(communicationsData.startsWith("#") && communicationsData.endsWith("~")) {
            communicationsData = communicationsData.replaceAll("#", "");
            communicationsData = communicationsData.replaceAll("~", "");
            String[] dataParts = communicationsData.split("\\+");

            if(dataParts.length == STRING_PARTS_SIZE) {
                //Set ChildInSeat
                isChildInSeat = setVariable(dataParts[FORCE_INDEX]);
                //Set Temperature Threshold
                isTempThresholdReached = setVariable(dataParts[TEMP_INDEX]);
                //Set AlarmState
                isAlarmState = setVariable(dataParts[ALARM_INDEX]);
                //Set Charging
                isCharging = setVariable(dataParts[CHARGING_INDEX]);
            } else {
                Toast.makeText(getApplicationContext(), "The string wasn't long enough",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "This string was invalid",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean setVariable(String inString) {
        float temp = Float.parseFloat(inString);
        if (temp == 0.0)
            return false;
        else
            return true;
    }
}
