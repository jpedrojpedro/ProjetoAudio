package br.com.jpedrojpedro.trabalhopwm.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Bluetooth {

    private static Bluetooth instance = null;
    private BluetoothAdapter myBluetoothAdapter;
    private String macAddress;
    private BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;

    private Bluetooth() {}

    public static Bluetooth getInstance() {
        if(instance == null)
            instance = new Bluetooth();
        return instance;
    }

    public boolean verifySupport() {

        this.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(this.myBluetoothAdapter == null)
            return false;
        return true;
    }

    public void startStreaming(InputStream song, Context context) {
        this.context = context;
        ConnectionControl cc = new ConnectionControl();
        cc.PlayWav(song);
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public BluetoothAdapter getMyBluetoothAdapter() {
        return myBluetoothAdapter;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public Context getContext() {
        return this.context;
    }

    public static UUID getMyUuid() {
        return MY_UUID;
    }
}

class ConnectionControl implements Runnable {

    // Debug reasons
    private static final String TAG = "PlayerWireless";

    ConnectionControl() {}

    private void errorExit(String title, String message){
        Toast.makeText(Bluetooth.getInstance().getContext(),
                title + " - " + message, Toast.LENGTH_SHORT).show();
    }

    public void PlayWav(InputStream is)
    {
        int bytesRead;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[10240];

            while ((bytesRead = is.read(b)) != -1)
                bos.write(b, 0, bytesRead);

            byte[] bytes = bos.toByteArray();

            if ( bytes != null ) {
                int aux = 0;
                String strAux = "";
                while(bytes.length > aux) {
                    if ( aux%14400 == 0) {
                        sendData(strAux);
                        strAux = "";
                    }
                    strAux += bytes[aux];
                    aux++;
                }
            }
        } catch(Exception e) {
            Toast.makeText(Bluetooth.getInstance().getContext(),
                    "Error starting draw.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(String message)
    {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Sending data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "...In onResume - Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);


        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        myBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }
}