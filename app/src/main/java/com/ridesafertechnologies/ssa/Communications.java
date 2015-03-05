package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 *
 */
public class Communications extends IntentService {

    Intent dataParserIntent;

    public Communications() {
        super("Communications");
    }

    private static final String DEFAULT_DATA_TOKEN = "#0+74.56+0+0+~";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mBluetoothDevice = null;

    //i've even tried to hardcode macaddress of my HC-06 unit
    String bluetoothDeviceMacAddy = "20:14:12:17:10:69";

    private static String dataToken = DEFAULT_DATA_TOKEN;

    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    public static String getDataToken() {
        return dataToken;
    }

    @Override
    public void onCreate() {
        dataParserIntent = new Intent(this, Data_Parser.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        findAndConnectSSA();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

    }

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
                    dataToken = new String(readBuf);
                    startService(dataParserIntent);
                    //populate a 'test' TextView with data pushed from Arduino
                    //THIS IS NOT WORKING for some reason.
                    //btTextView.setText(btString);
                    //Toast.makeText(getActivity(), btString, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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

    private void connectDevice(String address){
        //Log.d(TAG, "connectDevice address: " + address);

        if (mBluetoothAdapter == null) {
            //bluetoothTextView.setText("Not a bluetooth device");
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
}
