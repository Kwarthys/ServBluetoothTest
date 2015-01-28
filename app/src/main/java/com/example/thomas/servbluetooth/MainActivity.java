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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//pwalpwalpwal

public class MainActivity extends ActionBarActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int SOCKET_CONNECTED = 4;
    public static final int DATA_RECEIVED = 3;
    private String data;

    private ConnectionThread mBluetoothConnection = null;

    protected Button bConnect = null;
    protected Button bDisconnect = null;
    protected Button sendButton = null;
    protected BluetoothAdapter btAdapter;
    protected TextView logs = null;
    protected EditText editSend = null;

    protected LinearLayout send = null;

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

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            mBluetoothConnection = (ConnectionThread) msg.obj;
            data = "123456789";//(String) msg.obj;
            superLog("Data :" + data);
            Log.d(TAG, "Message envoyé");
            mBluetoothConnection.write(data.getBytes());
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
}

//UUID 35ab00d3-22c4-41a0-9aba-bf6ad3271bce

//UUID Blueterm 00001101-0000-1000-8000-00805F9B34FB

//UUID d23a5tse-5e4f-zerg-5hg8-5g54g7e5r1g4 Fait main.

//Such fantastic!!! Much wonderful!!!