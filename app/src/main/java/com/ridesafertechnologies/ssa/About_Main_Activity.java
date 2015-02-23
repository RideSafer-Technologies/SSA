package com.ridesafertechnologies.ssa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class About_Main_Activity extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutScreenFragment())
                    .commit();
        }

    } // END About_Main_Activity onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about_main, menu);
        return true;
    } // END onCreateOptionsMenu()

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(getApplicationContext(), Settings_Activity.class);
            startActivity(settingsActivity);
            return true;
        } else if (id == R.id.action_user_manual) {
            Intent userManualActivity = new Intent(getApplicationContext(), User_Manual_Activity.class);
            startActivity(userManualActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // END onOptionsItemSelected()

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class AboutScreenFragment extends Fragment {

        private final int DK_GREEN_VAL_RED = 7;
        private final int DK_GREEN_VAL_GREEN = 145;
        private final int DK_GREEN_VAL_BLUE = 7;

        View rootView;

        public AboutScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_about_main, container, false);

            updateText(Data_Parser.getIsChildInSeat(), Data_Parser.getIsTempThresholdReached(),
                    Data_Parser.getIsCharging());

            return rootView;
        }  // END PlaceholderFragment OnCreateView()

        public void updateText(boolean isChild, boolean isTemperature, boolean isBattery) {
            // Bind Views
            TextView bluetoothTextView = (TextView) rootView.findViewById(R.id.is_bluetooth_connectivity);
            TextView childTextView = (TextView) rootView.findViewById(R.id.is_child_in_seat);
            TextView temperatureTextView = (TextView) rootView.findViewById(R.id.is_temperature_threshold);
            TextView batteryTextView = (TextView) rootView.findViewById(R.id.is_battery_status);

            //Set Text of 'bluetoothTextView' with attributes
            bluetoothTextView.setText("Unknown");

            //Set Text of 'childTextView' with attributes
            if(isChild) {
                childTextView.setText("Child In Seat");
                childTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,DK_GREEN_VAL_BLUE));
            } else {
                childTextView.setText("Child Not In Seat");
                childTextView.setTextColor(Color.RED);
            }

            //Set Text of 'temperatureTextView' with attributes
            if(isTemperature) {
                temperatureTextView.setText("Temperature Threshold Surpassed");
                temperatureTextView.setTextColor(Color.RED);
            } else {
                temperatureTextView.setText("Temperature is Reasonable");
                temperatureTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,DK_GREEN_VAL_BLUE));
            }

            //Set Text of batteryTextView' with attributes
            if(isBattery) {
                batteryTextView.setText("Charging");
                batteryTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,DK_GREEN_VAL_BLUE));
            } else {
                batteryTextView.setText("Discharging");
                batteryTextView.setTextColor(Color.RED);
            }
        }

    } // END PlaceholderFragment


} // END About_Main_Activity
