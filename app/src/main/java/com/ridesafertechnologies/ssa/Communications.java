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

    public static boolean isConnectionStatus() {
        return connectionStatus;
    }

    public static void setConnectionStatus(boolean connectionStatus) {
        Communications.connectionStatus = connectionStatus;
    }

    private static boolean connectionStatus;



    private char delimiter = '~';

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
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
                    //connectedThread.run();
                    connectedThread.bluetoothLoop.run();
                    Toast.makeText(getApplicationContext(), "Connected to SSA", Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_READ:

                    //byte[] readBuf = (byte[]) msg.obj;
                    //dataToken = new String(readBuf, 0, 126);
                    dataToken = (String) msg.obj;
                    if(msg.arg1 > 0) {
                        Toast.makeText(getApplicationContext(), "SSA: " + dataToken, Toast.LENGTH_LONG).show();
                    }
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
                    //connectDevice(mBluetoothDevice.getAddress());
                    ConnectThread newSSAConnection = new ConnectThread(mBluetoothDevice);
                    newSSAConnection.run();
                }
            }
        }
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     *
     * @param address
     */
    /*
    private void connectDevice(String address){
        //Log.d(TAG, "connectDevice address: " + address);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
    
    */
    
    
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
            //ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
            //mConnectedThread.run();
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
                byte[] buffer;  // buffer store for the stream
                byte[] secondary;
                int bytes = 0; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while(bytes <= 26) {//i tried TRUE here, but i think its TOO labor intensive

                    try {
                        buffer = new byte[256]; // resets the buffer for new incoming data
                        secondary = new byte[256];
                        int start = 0;
                        
                        if(mmInStream.available() > 0) {
                            // Read from the InputStream
                            sleep(1000);
                            bytes = mmInStream.read(buffer);
                            sleep(1000);
                            bytes = mmInStream.read(buffer);
                            sleep(1000);
                            bytes = mmInStream.read(buffer);
                            sleep(1000);
                            for(int i = 0; i < buffer.length; i++)
                            {
                                if(buffer[i] == '#')
                                {
                                    start = i;
                                }
                            }
                            if(secondary.length < 25)
                            {
                                this.run();
                            }
                            System.arraycopy(buffer, start, secondary, 0, 26);
                            //if(mmInStream.available() > 10) {
                            String readMessage = new String(secondary, 0, 26);
                            // Send the obtained bytes to the UI activity
                            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, readMessage)
                                    .sendToTarget();
                        }
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "IOException e", Toast.LENGTH_LONG).show();
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
