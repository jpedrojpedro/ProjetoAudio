package br.com.jpedrojpedro.trabalhopwm.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import br.com.jpedrojpedro.trabalhopwm.activity.MainActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConnectionControl implements Runnable {

    // Debug reasons
    private static final String TAG = "PlayerWireless";

    private MainActivity ma;
    private BluetoothSocket btSocket = null;

    public ConnectionControl(MainActivity ma) {
        this.ma = ma;
        new Thread(this).start();
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
                    if (aux%1024 == 0) {
                        sendData(strAux);
                        strAux = "";
                    }
                    strAux += bytes[aux];
                    aux++;
                }
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "Sending Data");
        this.ma.handler.sendEmptyMessage(7);

        try {
            Bluetooth.getInstance().getOutStream().write(msgBuffer);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void run() {

        Log.d(TAG, "Attempting to connect bluetooth");
        this.ma.handler.sendEmptyMessage(0);

        BluetoothDevice device =
                Bluetooth.getInstance().getMyBluetoothAdapter().
                        getRemoteDevice(Bluetooth.getInstance().getMacAddress());

        try {
            this.btSocket = device.createRfcommSocketToServiceRecord(Bluetooth.getMyUuid());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            this.ma.handler.sendEmptyMessage(1);
        }

        Bluetooth.getInstance().getMyBluetoothAdapter().cancelDiscovery();

        Log.d(TAG, "Connecting");
        this.ma.handler.sendEmptyMessage(2);

        try {
            this.btSocket.connect();
            Log.d(TAG, "Connection established and data link opened");
            this.ma.handler.sendEmptyMessage(3);
        } catch (IOException e) {
            try {
                this.btSocket.close();
            } catch (IOException e2) {
                this.ma.handler.sendEmptyMessage(4);
            }
        }

        Log.d(TAG, "Creating Socket");
        this.ma.handler.sendEmptyMessage(5);

        try {
            Bluetooth.getInstance().setOutStream(this.btSocket.getOutputStream());
        } catch (IOException e) {
            this.ma.handler.sendEmptyMessage(6);
        }

        this.PlayWav(Bluetooth.getInstance().getInputStream());
    }
}