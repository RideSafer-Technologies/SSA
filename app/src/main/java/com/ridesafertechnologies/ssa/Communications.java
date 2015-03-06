package com.ridesafertechnologies.ssa;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 *
 */
public class Communications extends IntentService {

    // Intents to handle IntentServices
    private Intent dataParserIntent;

    // Constant static variables to reduce "Hard-Coding"
    private static final String DEFAULT_DATA_TOKEN = "#0+74.56+0+0+~";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    // Bluetooth controls
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;

    private static String dataToken = DEFAULT_DATA_TOKEN;

    /**
     * Default constructor for Communications IntentService
     */
    public Communications() {
        super("Communications");
    }

    /**
     *
     * @return
     */
    public static String getDataToken() {
        return dataToken;
    }

    /**
     *
     */
    public static void setDataToken(String inDataToken) {
        dataToken = inDataToken;
    }


    /**
     *
     */
    @Override
    public void onCreate() {
        dataParserIntent = new Intent(this, Data_Parser.class);
        }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        findAndConnectSSA();
        return Service.START_STICKY;
    }

    /**
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), "Test onHandleIntent", Toast.LENGTH_LONG).show();



    }

    /**
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {

        Toast.makeText(getApplicationContext(), "Test onBind", Toast.LENGTH_LONG).show();

        return null;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {

        Toast.makeText(getApplicationContext(), "Test onDestroy", Toast.LENGTH_LONG).show();


    }

    /**
     *
     */
      android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SUCCESS_CONNECT:
                    //TODO do something here that pushes to parser
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
                    connectedThread.bluetoothLoop.run();
                    break;
                case MESSAGE_READ:
                    Toast.makeText(getApplicationContext(), "Case " + MESSAGE_READ + " running", Toast.LENGTH_LONG).show();
                    byte[] readBuf = (byte[]) msg.obj;
                    for(int i = 0; i < readBuf.length; i++)
                        Toast.makeText(getApplicationContext(), "readBuf " + i + ": " + readBuf[i], Toast.LENGTH_SHORT).show();
                    dataToken = new String(readBuf);
                       Toast.makeText(getApplicationContext(), "dataToken: " + dataToken, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };



    /**
     *
     */
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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    /**
     *
     * @param address
     */
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

    /**
     *
     */
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

        /**
         *
         */
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

        /**
         *
         * @param mmSocket2
         */
        private void manageConnectedSocket(BluetoothSocket mmSocket2){
            //intentionally left blank
        }

        /**
         *
         */
        // Will cancel an in-progress connection, and close the socket
        public void cancel() {

            Toast.makeText(getApplicationContext(), "Test ConnectThread cancel", Toast.LENGTH_LONG).show();

            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /**
     *
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        /**
         *
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Test ConnectedThread Constructor: IOException e", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public Runnable bluetoothLoop = new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "IOException e", Toast.LENGTH_LONG).show();
                }
            }
        };

        /**
         *
         * @param bytes
         */
        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {

            Toast.makeText(getApplicationContext(), "Test ConnectedThread write", Toast.LENGTH_LONG).show();

            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /**
         *
         */
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {

            Toast.makeText(getApplicationContext(), "Test ConnectedThread cancel", Toast.LENGTH_LONG).show();

            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
