package br.com.jpedrojpedro.trabalhopwm.connection;

import android.bluetooth.BluetoothAdapter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Bluetooth {

    private static Bluetooth instance = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OutputStream outStream = null;
    private InputStream inputStream = null;
    private BluetoothAdapter myBluetoothAdapter;
    private String macAddress;

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

    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public BluetoothAdapter getMyBluetoothAdapter() { return this.myBluetoothAdapter; }

    public String getMacAddress() { return this.macAddress; }

    public static UUID getMyUuid() { return MY_UUID; }

    public OutputStream getOutStream() { return this.outStream; }

    public void setOutStream(OutputStream stream) { this.outStream = stream; }

    public InputStream getInputStream() { return this.inputStream; }

    public void setInputStream(InputStream stream) { this.inputStream = stream; }
}