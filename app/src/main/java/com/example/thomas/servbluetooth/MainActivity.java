package com.example.thomas.servbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

//pwalpwalpwal

public class MainActivity extends ActionBarActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int SOCKET_CONNECTED = 4;
    public static final int DATA_RECEIVED = 3;

    private ConnectionThread mBluetoothConnection = null;

    protected Spinner spinner = null;
    protected ArrayAdapter<CharSequence> arraySpinner;
    protected ListView listTrames = null;
    protected ArrayAdapter<String> arrayList;

    protected Button bConnect = null;
    protected Button bDisconnect = null;
    protected Button sendButton = null;
    protected BluetoothAdapter btAdapter;
    protected TextView logs = null;
    protected EditText editSend = null;

    protected LinearLayout send = null;

    protected int vpan1 = 0;
    protected int vpan2 = 0;
    protected int vpan3 = 0;
    protected int vpan4 = 0;
    protected int vpan5 = 0;

    protected BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refreshButtons();
        }
    };

    protected BroadcastReceiver discovering = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            tost("N'a trouvé " + remoteDevice.getName());
            Log.d(TAG, remoteDevice.getName() + " Found");
        }
    };

    protected Handler tempoH = new Handler();
    protected Runnable envoiTrames = new Runnable() {
        @Override
        public void run() {
            String message = getTrame(spinner.getSelectedItem().toString());
            mBluetoothConnection.write(message.getBytes());
            Log.d(TAG, "Envoi effectué");


            tempoH.postDelayed(this, 1000);
        }
    };

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            String data;
            if (msg.what == SOCKET_CONNECTED)
            {
                mBluetoothConnection = (ConnectionThread) msg.obj;
                tempoH.postDelayed(envoiTrames,1000);
            }
            else if (msg.what == DATA_RECEIVED)
            {
                data = (String) msg.obj;
                superLog("Data : " + data);
                Log.d(TAG, "Message Reçu");
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
    }




    private void setUpUI()
    {
        Log.d(TAG,"Setting UI");
        initVariables();
        refreshButtons();


        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
                startActivityForResult(new Intent(beDiscoverable), 1);
            }
        });

        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.disable();
                send.setVisibility(View.GONE);
                tempoH.removeCallbacks(envoiTrames);
            }
        });

    }

    private void initVariables()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bConnect = (Button)findViewById(R.id.bConnect);
        bDisconnect = (Button)findViewById(R.id.bDisconnect);
        bConnect.setVisibility(View.GONE);
        bDisconnect.setVisibility(View.GONE);
        sendButton = (Button)findViewById(R.id.sendButton);
        editSend = (EditText)findViewById(R.id.editSend);

        spinner = (Spinner)findViewById(R.id.spinner);
        arraySpinner = ArrayAdapter.createFromResource(this,
                R.array.IDTrames, android.R.layout.simple_spinner_item);
        arraySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arraySpinner);

        listTrames = (ListView)findViewById(R.id.listTrames);
        arrayList = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,0);
        listTrames.setAdapter(arrayList);

        logs = (TextView)findViewById(R.id.textLog);

        send = (LinearLayout)findViewById(R.id.layoutSend);
        send.setVisibility(View.GONE);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothState, filter);
    }

    private void refreshButtons()
    {
        Log.d(TAG,"Refreshing buttons");

        if(btAdapter.isEnabled())
        {
            bDisconnect.setVisibility(View.VISIBLE);
            bConnect.setVisibility(View.GONE);
        }
        else
        {
            bConnect.setVisibility(View.VISIBLE);
            bDisconnect.setVisibility(View.GONE);
        }
        Log.d(TAG,"Buttons refreshed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1)
        {
            tost("Starting Discovery");
            findDevices();

            Log.d(TAG,"Run connexion");
            send.setVisibility(View.VISIBLE);
            superLog("Thread Lancé");

            new AcceptThread(mHandler).start();
        }
        refreshButtons();
    }

    private void findDevices()
    {
        if(btAdapter.startDiscovery())
        {
            registerReceiver(discovering, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            tost("Discovery in progress");
        }
    }

    private void tost(String text)
    {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void superLog(String log)
    {
        if(log!=null)
        {
            Log.d(TAG, log);
            logs.setText(log);
        }
    }

    private String zeros(String manger)
    {
        String zeros = "";
        int taille = manger.length();
        int truc = 4 - taille;
        for(int i = 0; i< truc ; i++)
            zeros += "0";
        return zeros;
    }


    private String getTrame(String nom)
    {
        switch (nom)
        {
            case "Panneau 1" : return "52" + zeros(String.valueOf(vpan1)) + vpan1 + "++";
            case "Panneau 2" : return "51" + zeros(String.valueOf(vpan2)) + vpan2 + "++";
            case "Panneau 3" : return "53" + zeros(String.valueOf(vpan3)) + vpan3 + "++";
            case "Panneau 4" : return "56" + zeros(String.valueOf(vpan4)) + vpan4 + "++";
            case "Panneau 5" : return "57" + zeros(String.valueOf(vpan5)) + vpan5 + "++";
        }
        return null;
    }
}

//UUID 35ab00d3-22c4-41a0-9aba-bf6ad3271bce

//UUID Blueterm 00001101-0000-1000-8000-00805F9B34FB

//UUID d23a5tse-5e4f-zerg-5hg8-5g54g7e5r1g4 Fait main.

//Such fantastic!!! Much wonderful!!!