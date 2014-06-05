package br.com.jpedrojpedro.trabalhopwm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.View.OnClickListener;
import br.com.jpedrojpedro.trabalhopwm.connection.Bluetooth;

public class MainActivity extends Activity implements OnClickListener
{

	private Button sendButton;
    private EditText macAddress;
    private TextView labelStatus;
    private RadioButton radioMambo;
    private RadioButton radioWindows;
    private String address = "20:13:06:19:07:50"; //115000 bps
	
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
        this.radioMambo = (RadioButton) findViewById(R.id.radio_song_mambo);
        this.radioWindows = (RadioButton) findViewById(R.id.radio_song_windows);

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
        this.address = this.macAddress.getText().toString();
        Context context = view.getContext();
        Bluetooth.getInstance().setMacAddress(this.address);
        if(this.radioMambo.isChecked())
            Bluetooth.getInstance().setInputStream(
                    context.getResources().openRawResource(R.raw.mambo)
            );
        else
            Bluetooth.getInstance().setInputStream(
                    context.getResources().openRawResource(R.raw.windows)
            );
        Bluetooth.getInstance().startStreaming();
    }
}