package com.ridesafertechnologies.ssa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class About_Main_Activity extends ActionBarActivity {

    private static final int BLUETOOTH_ALERT = 10;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Intent communicationsIntent;
    Intent dataParserIntent;

    /**
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutScreenFragment())
                    .commit();
        }
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        communicationsIntent = new Intent(this, Communications.class);
        dataParserIntent = new Intent(this, Data_Parser.class);

        // Start the Communications class background Service
       //startService(communicationsIntent);
    } // END About_Main_Activity onCreate

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
            showDialog(BLUETOOTH_ALERT);
    }

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
     *
     */
    public static class AboutScreenFragment extends Fragment {

        private final int DK_GREEN_VAL_RED = 7;
        private final int DK_GREEN_VAL_GREEN = 145;
        private final int DK_GREEN_VAL_BLUE = 7;
        protected static final int SUCCESS_CONNECT = 0;
        protected static final int MESSAGE_READ = 1;

        BluetoothAdapter mBluetoothAdapter = null;
        BluetoothDevice mBluetoothDevice = null;
        TextView bluetoothTextView;
        TextView childTextView;
        TextView temperatureTextView;
        TextView batteryTextView;
        static TextView btTextView;
        private static final String DEFAULT_DATA_TOKEN = "#0+74.56+0+0+~";
        private static String btString = DEFAULT_DATA_TOKEN;

        public static String getBtString() { return btString; }
        public static void setBtString(String in) { btString = in; }

        //i've even tried to hardcode macaddress of my HC-06 unit
        String bluetoothDeviceMacAddy = "20:14:12:17:10:69";

        // Root View for View Control
        View rootView;

        private static final String TAG = "dataServiceUpdate";
        private Intent updateIntent;
        
        //handler
        android.os.Handler mHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS_CONNECT:
                        //TODO do something here that pushes to parser
                        ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        btString = new String(readBuf);
                        //populate a 'test' TextView with data pushed from Arduino
                        //THIS IS NOT WORKING for some reason.
                        btTextView.setText(btString);
                        //Toast.makeText(getActivity(), btString, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        public AboutScreenFragment() {
        }

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

            init(); //initialize all components

            // Update the screen information
            findAndConnectSSA();
            updateIntent = new Intent(rootView.getContext(), dataService.class);

            return rootView;
        }  // END PlaceholderFragment OnCreateView()



        public void findAndConnectSSA() {
            /*
             * so far connectivity works ONLY if bluetooth is initially on.
             */

            /*
             * Bluetooth related connectivity for hardcoded MAC address
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            connectDevice(bluetoothDeviceMacAddy);
            ConnectThread newConnection = new ConnectThread(mBluetoothDevice);
            newConnection.run();
            */

            //finds BT named "SSA" from list and connects to it
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    if (device.getName().equals("SSA")) {
                        mBluetoothDevice = device;
                        connectDevice(mBluetoothDevice.getAddress());
                        ConnectThread newSSAConnection = new ConnectThread(mBluetoothDevice);
                        newSSAConnection.run();
                    }
                }
            }
        }

        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent updateIntent){
                updateUI(updateIntent);
            }
        };

        private void connectDevice(String address){
            //Log.d(TAG, "connectDevice address: " + address);

            if (mBluetoothAdapter == null) {
                bluetoothTextView.setText("Not a bluetooth device");
            }
            mBluetoothDevice=mBluetoothAdapter.getRemoteDevice(address);
            try {
                mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (  IOException e) {
                e.printStackTrace();
            }
        }

        private class ConnectThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final BluetoothDevice mmDevice;

            public ConnectThread(BluetoothDevice device) {
                // Use a temporary object that is later assigned to mmSocket,
                // because mmSocket is final
                BluetoothSocket tmp = null;
                mmDevice = device;

                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                    // MY_UUID is the app's UUID string, also used by the server code
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) { }
                mmSocket = tmp;
            }

            public void run() {
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();

                try {
                    // Connect the device through the socket. This will block
                    // until it succeeds or throws an exception
                    mmSocket.connect();
                } catch (IOException connectException) {
                    // Unable to connect; close the socket and get out
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) { }
                    return;
                }

                // Do work to manage the connection (in a separate thread)
                mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
            }

            private void manageConnectedSocket(BluetoothSocket mmSocket2){
                //intentionally left blank
            }
            // Will cancel an in-progress connection, and close the socket
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) { }
            }
        }

        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;


            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams, using temp objects because
                // member streams are final
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) { }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer);
                        // Send the obtained bytes to the UI activity
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    } catch (IOException e) {
                        break;
                    }
                }
            }

            /* Call this from the main activity to send data to the remote device */
            public void write(byte[] bytes) {
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) { }
            }

            /* Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) { }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            updateText(Data_Parser.getIsChildInSeat(), Data_Parser.getIsTempThresholdReached(),
                    Data_Parser.getIsCharging());
            getActivity().startService(updateIntent);
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(dataService.DATA_SERVICE_ACTION));
        }

        @Override
        public void onPause() {
            super.onPause();
            getActivity().unregisterReceiver(broadcastReceiver);
            getActivity().stopService(updateIntent);
        }

        public void init(){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothTextView = (TextView) rootView.findViewById(R.id.is_bluetooth_connectivity);
            childTextView = (TextView) rootView.findViewById(R.id.is_child_in_seat);
            temperatureTextView = (TextView) rootView.findViewById(R.id.is_temperature_threshold);
            batteryTextView = (TextView) rootView.findViewById(R.id.is_battery_status);
        }

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
        } // END updateText

        private void updateUI(Intent updateIntent) {

            boolean isChildInSeat = updateIntent.getBooleanExtra("ChildInSeat", false);
            boolean isTemperatureThreshold = updateIntent.getBooleanExtra("TemperatureThreshold", false);
            boolean isAlarmState = updateIntent.getBooleanExtra("AlarmState", false);
            boolean isCharging = updateIntent.getBooleanExtra("Charging", false);

            updateText(isChildInSeat, isTemperatureThreshold,isCharging);
        }
    } // END AboutScreenFragment

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case BLUETOOTH_ALERT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth Verification");
                builder.setMessage("The Bluetooth is turned off. The SSA App requires that the " +
                        "Bluetooth be turned on in order to function properly.");
                builder.setCancelable(true);
                builder.setPositiveButton("Open Bluetooth Settings", new OkOnClickListener());
                builder.setNegativeButton("Cancel", new CancelOnClickListener());
                AlertDialog bluetoothDialog = builder.create();
                bluetoothDialog.show();
        }
        return super.onCreateDialog(id);
    } // END of Dialog

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            Toast.makeText(getApplicationContext(), "The SSA App will not work correctly without " +
                    "the bluetooth connection", Toast.LENGTH_LONG).show();
        }
    }

    private final class OkOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialogInterface, int which) {
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
        }
    }
} // END About_Main_Activity
