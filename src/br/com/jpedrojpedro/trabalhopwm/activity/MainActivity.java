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
    private EditText macAdress;
	private TextView labelMessage;
    private String address = "20:13:06:19:07:50"; //115000 bps
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.labelMessage = (TextView) findViewById(R.id.label_message);
        this.macAdress = (EditText) findViewById(R.id.input_address);
        this.macAdress.setText(this.address);
        this.sendButton = (Button) findViewById(R.id.btn_confirm);
        this.sendButton.setOnClickListener(this);

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
        this.address = this.macAdress.getText().toString();
        Context context = view.getContext();
        Bluetooth.getInstance().setMacAddress(this.address);
        Bluetooth.getInstance().startStreaming(
                context.getResources().openRawResource(R.raw.mambo),
                context);
    }
}