package com.example.thomas.servbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends ActionBarActivity
{
    private UUID MY_UUID = UUID.fromString("35ab00d3-22c4-41a0-9aba-bf6ad3271bce");
    protected BluetoothServerSocket servSocket;
    protected Button bConnect = null;
    protected Button bDisconnect = null;
    protected BluetoothAdapter btAdapter;
    protected TextView logs = null;

    protected String address = "";
    protected String name = "";

    protected ListView listViewFound;
    protected ArrayAdapter<String> listFoundAdapter;

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
            String newContent = remoteDevice.getName() + "\n" + remoteDevice.getAddress();
            int size = listFoundAdapter.getCount();
            boolean nouveau = true;
            for(int i = 0; i<size; i++)
            {
                String temp = listFoundAdapter.getItem(i);
                if(temp.equals(newContent))
                {
                    nouveau = false;
                }
            }
            if(nouveau)
                listFoundAdapter.add(newContent);
            tost("N'a trouvé " + remoteDevice.getName());
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
        initVariables();
        refreshButtons();


        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
                startActivityForResult(new Intent(beDiscoverable), 1);
                listFoundAdapter.clear();

                listFoundAdapter.add("Found\nmanger");
                listFoundAdapter.add("Manger\nFound");
                listFoundAdapter.add("Found\nFound");
            }
        });

        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.disable();
                listFoundAdapter.clear();
            }
        });

        listViewFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String manger = listFoundAdapter.getItem(position);
                String[] leSplit;
                leSplit = manger.split("\n");
                name = leSplit[0];
                address = leSplit[1];
                logs.setText("this is a log :" + name + " + " + address);
                startConnexion();
            }
        });
    }

    private void startConnexion()
    {
        BluetoothServerSocket tmp = null;
        try {
            tmp = btAdapter.listenUsingRfcommWithServiceRecord("BluetoothTryHard", MY_UUID);
        } catch (IOException e) { tost("planté"); }
        servSocket = tmp;
    }

    private void initVariables()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        bConnect = (Button)findViewById(R.id.bConnect);
        bDisconnect = (Button)findViewById(R.id.bDisconnect);
        logs = (TextView)findViewById(R.id.textLog);
        bConnect.setVisibility(View.GONE);
        bDisconnect.setVisibility(View.GONE);

        listFoundAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewFound = (ListView)findViewById(R.id.listViewFound);
        listViewFound.setAdapter(listFoundAdapter);

        listViewFound.setVisibility(View.GONE);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothState, filter);
    }

    private void refreshButtons()
    {

        if(btAdapter.isEnabled())
        {
            bDisconnect.setVisibility(View.VISIBLE);
            bConnect.setVisibility(View.GONE);
            listViewFound.setVisibility(View.VISIBLE);
        }
        else
        {
            bConnect.setVisibility(View.VISIBLE);
            bDisconnect.setVisibility(View.GONE);
            listViewFound.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1)
        {
            tost("Starting Discovery");
            findDevices();
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
}

//UUID 35ab00d3-22c4-41a0-9aba-bf6ad3271bce

//UUID d23a5tse-5e4f-zerg-5hg8-5g54g7e5r1g4 Fait main.

//Such fantastic!!! Much wonderful!!!