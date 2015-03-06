package com.ridesafertechnologies.ssa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ridesafertechnologies.ssa.util.dataService;
import com.ridesafertechnologies.ssa.util.demoMode;

/**
 *
 */
public class About_Main_Activity extends ActionBarActivity {

    // Constant static variables to reduce "Hard-Coding"
    private static final int BLUETOOTH_ALERT = 10;
    private static final int DEMO_MODE_ALERT = 20;
//    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // demo mode boolean
    private static boolean isDemoMode = false;

    // Intents to handle IntentServices
    Intent communicationsIntent;
    Intent demoModeIntent;

    Persistent_Notification persistentNotification;

    /**
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        // Set the content for the Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutScreenFragment())
                    .commit();
        }
        // Initialize Preference Manager
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Initialize Intents
        communicationsIntent = new Intent(this, Communications.class);
        demoModeIntent = new Intent(this, demoMode.class);

        persistentNotification = new Persistent_Notification();
        persistentNotification.notify(this, "SSA", 1);

        showDialog(DEMO_MODE_ALERT);

    } // END About_Main_Activity onCreate

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Initialize Bluetooth Adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the bluetooth adapter isn't turned on alert the user
        if(!mBluetoothAdapter.isEnabled())
            showDialog(BLUETOOTH_ALERT);
    } // END About_Main_Activity onResume

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about_main, menu);
        return true;
    } // END onCreateOptionsMenu()

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button,
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
     *
     */
    public static class AboutScreenFragment extends Fragment {

        // Constant variables to reduce "Hard-Coding"
        private final int DK_GREEN_VAL_RED = 7;
        private final int DK_GREEN_VAL_GREEN = 145;
        private final int DK_GREEN_VAL_BLUE = 7;


        // Bluetooth controls
        private BluetoothAdapter mBluetoothAdapter = null;
//        private BluetoothDevice mBluetoothDevice = null;

        // Will connect to text views on fragment
        private TextView bluetoothTextView;
        private TextView childTextView;
        private TextView temperatureTextView;
        private TextView batteryTextView;

        // Root View for View Control
        private View rootView;

        // Intent to handle display updates
        private Intent updateIntent;
        Intent dataParserIntent;
        Intent notificationTrigger;



        /**
         * Default Constructor - Required for class
         */
        public AboutScreenFragment() { // Do Nothing
        } // END AboutScreenFragment Constructor

        /**
         *
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Create a root view to connect to the outer class
            rootView = inflater.inflate(R.layout.fragment_about_main, container, false);

            // Initialize all components
            init();


            // Update screen contents
            updateIntent = new Intent(rootView.getContext(), dataService.class);
            dataParserIntent = new Intent(rootView.getContext(), Data_Parser.class);
            notificationTrigger = new Intent(rootView.getContext(), Notification_Trigger.class);

            return rootView;
        }  // END AboutScreenFragment OnCreateView()


        /**
         *
         */
        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent updateIntent){
                updateUI(updateIntent);
            }
        }; // END AboutScreenFragment broadcastReceiver


        @Override
        public void onResume() {
            super.onResume();
            getActivity().startService(dataParserIntent);
            getActivity().startService(notificationTrigger);
            getActivity().startService(updateIntent);
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(dataService.DATA_SERVICE_ACTION));
        } // END AboutScreenFragment onResume

        @Override
        public void onPause() {
            super.onPause();
            getActivity().unregisterReceiver(broadcastReceiver);
            getActivity().stopService(updateIntent);
        } // END AboutScreenFragment onPause

        public void init(){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothTextView = (TextView) rootView.findViewById(R.id.is_bluetooth_connectivity);
            childTextView = (TextView) rootView.findViewById(R.id.is_child_in_seat);
            temperatureTextView = (TextView) rootView.findViewById(R.id.is_temperature_threshold);
            batteryTextView = (TextView) rootView.findViewById(R.id.is_battery_status);
        } // END AboutScreenFragment init

        public void updateText(boolean isChild, boolean isTemperature, boolean isBattery) {
            //Set Text of 'bluetoothTextView' with attributes
            if(mBluetoothAdapter == null) {
                bluetoothTextView.setText("Device is NOT Supported");
                bluetoothTextView.setTextColor(Color.RED);
            } else if (mBluetoothAdapter.isEnabled()) {
                bluetoothTextView.setText("Enabled");
                bluetoothTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,
                        DK_GREEN_VAL_BLUE));
            } else {
                bluetoothTextView.setText("Disabled");
                bluetoothTextView.setTextColor(Color.RED);
            }

            //Set Text of 'childTextView' with attributes
            if(isChild) {
                childTextView.setText("Child in Seat");
                childTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,
                        DK_GREEN_VAL_BLUE));
            } else {
                childTextView.setText("Child Not in Seat");
                childTextView.setTextColor(Color.RED);
            }

            //Set Text of 'temperatureTextView' with attributes
            if(isTemperature) {
                temperatureTextView.setText("Threshold Surpassed");
                temperatureTextView.setTextColor(Color.RED);
            } else {
                temperatureTextView.setText("Within Threshold");
                temperatureTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,
                        DK_GREEN_VAL_BLUE));
            }

            //Set Text of batteryTextView' with attributes
            if(isBattery) {
                batteryTextView.setText("Charging");
                batteryTextView.setTextColor(Color.rgb(DK_GREEN_VAL_RED,DK_GREEN_VAL_GREEN,
                        DK_GREEN_VAL_BLUE));
            } else {
                batteryTextView.setText("Discharging");
                batteryTextView.setTextColor(Color.RED);
            }

            getActivity().startService(notificationTrigger);
        } // END AboutScreenFragment updateText

        private void updateUI(Intent updateIntent) {

            boolean isChildInSeat = updateIntent.getBooleanExtra("ChildInSeat", false);
            boolean isTemperatureThreshold = updateIntent.getBooleanExtra("TemperatureThreshold", false);
            boolean isAlarmState = updateIntent.getBooleanExtra("AlarmState", false);
            boolean isCharging = updateIntent.getBooleanExtra("Charging", false);

            updateText(isChildInSeat, isTemperatureThreshold,isCharging);
        } // END AboutScreenFragment updateUI

    } // END AboutScreenFragment

    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case BLUETOOTH_ALERT:
                builder.setTitle("Bluetooth Verification");
                builder.setMessage("The Bluetooth is turned off. The SSA App requires that the " +
                        "Bluetooth be turned on in order to function properly.");
                builder.setCancelable(true);
                builder.setPositiveButton("Open Bluetooth Settings", new OkOnClickListener());
                builder.setNegativeButton("Cancel", new CancelOnClickListener());
                AlertDialog bluetoothDialog = builder.create();
                bluetoothDialog.show();
                break;
            case DEMO_MODE_ALERT:
                builder.setTitle("Demo Mode");
                builder.setMessage("Would you like to enable the demonstration mode?");
                builder.setCancelable(true);
                builder.setPositiveButton("Start Demo", new OkDemoOnClickListener());
                builder.setNegativeButton("Cancel", new CancelDemoOnClickListener());
                AlertDialog demoDialog = builder.create();
                demoDialog.show();
                break;
        }
        return super.onCreateDialog(id);
    } // END of Dialog

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            Toast.makeText(getApplicationContext(), "The SSA App will not work correctly without " +
                    "the bluetooth connection working.", Toast.LENGTH_LONG).show();
        }
    } // END CancelOnClickListener

    private final class OkOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        }
    } // END OkOnClickListener

    private final class CancelDemoOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            isDemoMode = false;
            startService(communicationsIntent);
        }
    } // END CancelDemoOnClickListener

    private final class OkDemoOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            isDemoMode = true;
            startService(demoModeIntent);
        }
    } // END OkDemoOnClickListener

    @Override
    protected void onDestroy() {
        super.onDestroy();
        persistentNotification.cancel(this);
        stopService(demoModeIntent);
        stopService(communicationsIntent);
    }

} // END About_Main_Activity
