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
    private static final float HIGH_VALUE = 80;
    private static final float LOW_VALUE = 50;
    private static final float NO_VALUE = 0;



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

    public static boolean getIsAlarmState() {
        return isAlarmState;
    }

    public static boolean getIsCharging() {
        return isCharging;
    }

    public Data_Parser() {
        super("Data_Parser");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        testData();

        String communicationsData = About_Main_Activity.AboutScreenFragment.getBtString();

        if(communicationsData.startsWith("#") && communicationsData.endsWith("~")) {
            communicationsData = communicationsData.replaceAll("#", "");
            communicationsData = communicationsData.replaceAll("~", "");
            String[] dataParts = communicationsData.split("\\+");

            if(dataParts.length == STRING_PARTS_SIZE) {
                //Set ChildInSeat
                isChildInSeat = setVariable(dataParts[FORCE_INDEX], FORCE_INDEX);
                //Set Temperature Threshold
                isTempThresholdReached = setVariable(dataParts[TEMP_INDEX], TEMP_INDEX);
                //Set AlarmState
                isAlarmState = setVariable(dataParts[ALARM_INDEX], ALARM_INDEX);
                //Set Charging
                isCharging = setVariable(dataParts[CHARGING_INDEX], CHARGING_INDEX);
            } else {
                Toast.makeText(getApplicationContext(), "The string wasn't long enough",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "This string was invalid",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean setVariable(String inString, int index) {
        float temp = Float.parseFloat(inString);
        if (index == TEMP_INDEX) {
            if(temp > HIGH_VALUE || temp < LOW_VALUE)
                return true;
            else
                return false;
        } else if (temp != NO_VALUE)
            return true;
        else
            return false;
    }

    private static int num = 0;

    private void testData() {
        final String str1 = "#0+0+0+0+~";
        final String str2 = "#500+75+1+1+~";
        final String str3 = "#0+0+0+0+~";
        final String str4 = "#456+0+1+1+~";

        switch (num) {
            case 0:
                About_Main_Activity.AboutScreenFragment.setBtString(str1);
                break;
            case 1:
                About_Main_Activity.AboutScreenFragment.setBtString(str2);
                break;
            case 2:
                About_Main_Activity.AboutScreenFragment.setBtString(str3);
                break;
            case 3:
                About_Main_Activity.AboutScreenFragment.setBtString(str4);
                break;
        }

        if(num > 3)
            num = 0;
        else
            num++;
    }

}
