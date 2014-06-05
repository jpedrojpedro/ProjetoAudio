package br.com.jpedrojpedro.trabalhopwm.connection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import br.com.jpedrojpedro.trabalhopwm.activity.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Bluetooth {

    private static Bluetooth instance = null;
    private BluetoothAdapter myBluetoothAdapter;
    private String macAddress;
    private BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    private OutputStream outStream = null;
    private InputStream inputStream = null;
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

    public void startStreaming() {
        new ConnectionControl();
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

    public static UUID getMyUuid() {
        return MY_UUID;
    }

    public OutputStream getOutStream() { return this.outStream; }

    public void setOutStream(OutputStream stream) { this.outStream = stream; }

    public InputStream getInputStream() { return this.inputStream; }

    public void setInputStream(InputStream stream) { this.inputStream = stream; }

    public Context getContext() { return this.context; }
}

class ConnectionControl implements Runnable {

    // Debug reasons
    private static final String TAG = "PlayerWireless";

    ConnectionControl() {
        new Thread(this).start();
    }

    private void errorExit(String title, String message) {
        Toast.makeText(Bluetooth.getInstance().getContext(),
                title + " - " + message, Toast.LENGTH_SHORT).show();
    }

    public void PlayWav(InputStream is) {
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
                    if ( aux%1024 == 0) {
                        sendData(strAux);
                        strAux = "";
                    }
                    strAux += bytes[aux];
                    aux++;
                }
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(Bluetooth.getInstance().getContext(),
                    "Error sending song",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "Sending Data");

        try {
            Bluetooth.getInstance().getOutStream().write(msgBuffer);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void run() {

        Log.d(TAG, "Attempting to connect bluetooth");

        BluetoothDevice device =
                Bluetooth.getInstance().getMyBluetoothAdapter().
                        getRemoteDevice(Bluetooth.getInstance().getMacAddress());

        try {
            Bluetooth.getInstance().setBtSocket(
                    device.createRfcommSocketToServiceRecord(Bluetooth.getMyUuid()));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        Bluetooth.getInstance().getMyBluetoothAdapter().cancelDiscovery();

        Log.d(TAG, "Connecting");

        try {
            Bluetooth.getInstance().getBtSocket().connect();
            Log.d(TAG, "Connection established and data link opened");
        } catch (IOException e) {
            try {
                Bluetooth.getInstance().getBtSocket().close();
            } catch (IOException e2) {
                errorExit(TAG, e2.getMessage());
            }
        }

        Log.d(TAG, "Creating Socket");

        try {
            Bluetooth.getInstance().setOutStream(
                    Bluetooth.getInstance().getBtSocket().getOutputStream());
        } catch (IOException e) {
            errorExit(TAG, e.getMessage());
        }

        this.PlayWav(Bluetooth.getInstance().getInputStream());
    }
}