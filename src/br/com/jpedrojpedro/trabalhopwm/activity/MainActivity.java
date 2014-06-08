package br.com.jpedrojpedro.trabalhopwm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.*;
import android.view.View;
import android.view.View.OnClickListener;
import br.com.jpedrojpedro.trabalhopwm.connection.Bluetooth;
import br.com.jpedrojpedro.trabalhopwm.connection.ConnectionControl;

public class MainActivity extends Activity implements OnClickListener
{
    // Debug reasons
    private static final String TAG = "PlayerWireless";

	private Button sendButton;
    private EditText macAddress;
    private TextView labelStatus;
    private RadioButton radioMambo;
    private RadioButton radioWindows;
    private RadioGroup radioGroup;
    private String address = "20:13:11:06:22:63"; //115000 bps

    private String messages[] = {
            "Attempting to connect bluetooth",
            "Error creating socket",
            "Connecting",
            "Connection established and data link opened",
            "Error closing socket",
            "Creating Socket",
            "Error getting stream data",
            "Sending Data"
    };

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            addLabelStatus(messages[msg.what]);
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.macAddress = (EditText) findViewById(R.id.input_address);
        this.macAddress.setText(this.address);
        this.sendButton = (Button) findViewById(R.id.btn_confirm);
        this.sendButton.setOnClickListener(this);
        this.labelStatus = (TextView) findViewById(R.id.label_status_demand);
        this.labelStatus.setMovementMethod(new ScrollingMovementMethod());
        this.radioMambo = (RadioButton) findViewById(R.id.radio_song_mambo);
        this.radioWindows = (RadioButton) findViewById(R.id.radio_song_windows);
        this.radioGroup = (RadioGroup) findViewById(R.id.radio_song_group);

        if(!Bluetooth.getInstance().verifySupport())
        {
            Toast.makeText(this,
                    "Seu celular não suporta Bluetooth",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
   }

    @Override
    public void onClick(View view) {
        int selectedRadio;
        RadioButton selected;
        Bluetooth myBluetooth = Bluetooth.getInstance();
        this.address = this.macAddress.getText().toString();
        Context context = view.getContext();
        myBluetooth.setMacAddress(this.address);
        selectedRadio = this.radioGroup.getCheckedRadioButtonId();
        selected = (RadioButton) findViewById(selectedRadio);
        if(this.radioMambo.equals(selected))
            myBluetooth.setInputStream(
                    context.getResources().openRawResource(R.raw.mambo)
            );
        else if(this.radioWindows.equals(selected))
            myBluetooth.setInputStream(
                    context.getResources().openRawResource(R.raw.windows)
            );
        new ConnectionControl(this);
    }

    public void addLabelStatus(String labelStatus) {
        String aux = "";
        try {
            aux = this.labelStatus.getText().toString();
            aux += '\n';
            aux += labelStatus;

            int scrollAmount = this.labelStatus.getLayout().getLineTop(
                    this.labelStatus.getLineCount()) - this.labelStatus.getHeight();
            if (scrollAmount > 0)
                this.labelStatus.scrollTo(0, scrollAmount);
            else
                this.labelStatus.scrollTo(0, 0);

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            this.labelStatus.setText(aux);
        }
    }
}