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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
    protected Switch switchSend = null;
    protected Button bPlus = null;
    protected Button bMoins = null;
    protected LinearLayout layoutTrame = null;
    protected LinearLayout layoutList = null;
    protected TextView textValue = null;

    protected Button bConnect = null;
    protected Button bDisconnect = null;
    protected BluetoothAdapter btAdapter;
    protected TextView logs = null;

    protected int vpan1 = 0; protected Boolean statePan1 = false; String idPan1 = "52";
    protected int vpan2 = 0; protected Boolean statePan2 = false; String idPan2 = "51";
    protected int vpan3 = 0; protected Boolean statePan3 = false; String idPan3 = "53";
    protected int vpan4 = 0; protected Boolean statePan4 = false; String idPan4 = "56";
    protected int vpan5 = 0; protected Boolean statePan5 = false; String idPan5 = "57";
    protected int vpanGlobal = 0; protected Boolean statePanGlobal = false; String idPanGlobal = "58";
    protected int vTpanAvant = 0; protected Boolean stateTpanAvant = false; String idTpanAvant = "11";
    protected int vTpanMillieu = 0; protected Boolean stateTpanMillieu = false; String idTpanMillieu = "12";
    protected int vTpanArrière = 0; protected Boolean stateTpanArrière = false; String idTpanArrière = "13";

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

            if (statePan1)
            {
                String message = getTrame("Panneau 1");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (statePan2)
            {
                String message = getTrame("Panneau 2");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (statePan3)
            {
                String message = getTrame("Panneau 3");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (statePan4)
            {
                String message = getTrame("Panneau 4");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (statePan5)
            {
                String message = getTrame("Panneau 5");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (statePanGlobal)
            {
                String message = getTrame("Panneau Global");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (stateTpanMillieu)
            {
                String message = getTrame("T pan Millieu");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (stateTpanArrière)
            {
                String message = getTrame("T pan Arrière");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
            if (stateTpanAvant)
            {
                String message = getTrame("T pan Avant");
                mBluetoothConnection.write(message.getBytes());
                Log.d(TAG, "Envoi effectué" + message);
            }
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
                layoutList.setVisibility(View.GONE);
                layoutTrame.setVisibility(View.GONE);
                tempoH.removeCallbacks(envoiTrames);
            }
        });

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerTrame("PLUS");
            }
        });

        bMoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changerTrame("MOINS");
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStates();
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

        textValue = (TextView)findViewById(R.id.textValue);

        switchSend = (Switch)findViewById(R.id.switchSend);
        bMoins = (Button)findViewById(R.id.buttonMoins);
        bPlus = (Button)findViewById(R.id.buttonPlus);

        spinner = (Spinner)findViewById(R.id.spinner);
        arraySpinner = ArrayAdapter.createFromResource(this,
                R.array.IDTrames, android.R.layout.simple_spinner_item);
        arraySpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arraySpinner);

        listTrames = (ListView)findViewById(R.id.listTrames);
        arrayList = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,0);
        listTrames.setAdapter(arrayList);

        layoutList = (LinearLayout)findViewById(R.id.layoutList);
        layoutTrame = (LinearLayout)findViewById(R.id.layoutTrame);
        layoutList.setVisibility(View.GONE);
        layoutTrame.setVisibility(View.GONE);

        logs = (TextView)findViewById(R.id.textLog);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothState, filter);
    }

    private void updateStates()
    {
        switch (spinner.getSelectedItem().toString())
        {
            case "Panneau 1" : statePan1 = switchSend.isChecked(); break;
            case "Panneau 2" : statePan2 = switchSend.isChecked(); break;
            case "Panneau 3" : statePan3 = switchSend.isChecked(); break;
            case "Panneau 4" : statePan4 = switchSend.isChecked(); break;
            case "Panneau 5" : statePan5 = switchSend.isChecked(); break;
            case "Panneau Global" : statePanGlobal = switchSend.isChecked(); break;
            case "T pan Avant" : stateTpanAvant = switchSend.isChecked(); break;
            case "T pan Arrière" : stateTpanArrière= switchSend.isChecked(); break;
            case "T pan Millieu" : stateTpanMillieu = switchSend.isChecked(); break;
        }
        updateListe();

    }
    private void updateListe()
    {
        arrayList.clear();
        if(statePan1)
            arrayList.add("Panneau 1");
        if(statePan2)
            arrayList.add("Panneau 2");
        if(statePan3)
            arrayList.add("Panneau 3");
        if(statePan4)
            arrayList.add("Panneau 4");
        if(statePan5)
            arrayList.add("Panneau 5");
        if(statePanGlobal)
            arrayList.add("Panneau Global");
        if(stateTpanArrière)
            arrayList.add("T pan Arrière");
        if(stateTpanAvant)
            arrayList.add("T pan Avant");
        if(stateTpanMillieu)
            arrayList.add("T pan Millieu");
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

            Log.d(TAG, "Run connexion");
            layoutList.setVisibility(View.VISIBLE);
            layoutTrame.setVisibility(View.VISIBLE);
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

    private void refreshSpinner()
    {
        if (textValue!=null && switchSend!=null)
        {
            switch (spinner.getSelectedItem().toString()) {
                case "Panneau 1":
                    switchSend.setChecked(statePan1);
                    textValue.setText(String.valueOf(vpan1));
                    break;
                case "Panneau 2":
                    switchSend.setChecked(statePan2);
                    textValue.setText(String.valueOf(vpan2));
                    break;
                case "Panneau 3":
                    switchSend.setChecked(statePan3);
                    textValue.setText(String.valueOf(vpan3));
                    break;
                case "Panneau 4":
                    switchSend.setChecked(statePan4);
                    textValue.setText(String.valueOf(vpan4));
                    break;
                case "Panneau 5":
                    switchSend.setChecked(statePan5);
                    textValue.setText(String.valueOf(vpan5));
                    break;
                case "Panneau Global":
                    switchSend.setChecked(statePanGlobal);
                    textValue.setText(String.valueOf(vpanGlobal));
                    break;
                case "T pan Avant":
                    switchSend.setChecked(stateTpanAvant);
                    textValue.setText(String.valueOf(vTpanAvant));
                    break;
                case "T pan Arrière":
                    switchSend.setChecked(stateTpanArrière);
                    textValue.setText(String.valueOf(vTpanArrière));
                    break;
                case "T pan Millieu":
                    switchSend.setChecked(stateTpanMillieu);
                    textValue.setText(String.valueOf(vTpanMillieu));
                    break;
            }
        }

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
            case "Panneau 1" : return idPan1 + zeros(String.valueOf(vpan1)) + vpan1 + "++";
            case "Panneau 2" : return idPan2 + zeros(String.valueOf(vpan2)) + vpan2 + "++";
            case "Panneau 3" : return idPan3 + zeros(String.valueOf(vpan3)) + vpan3 + "++";
            case "Panneau 4" : return idPan4 + zeros(String.valueOf(vpan4)) + vpan4 + "++";
            case "Panneau 5" : return idPan5 + zeros(String.valueOf(vpan5)) + vpan5 + "++";
            case "Panneau Global" : return idPanGlobal + zeros(String.valueOf(vpanGlobal)) + vpanGlobal + "++";
            case "T pan Avant" : return idTpanAvant + zeros(String.valueOf(vTpanAvant)) + vTpanAvant + "++";
            case "T pan Arrière" : return idTpanArrière + zeros(String.valueOf(vTpanArrière)) + vTpanArrière + "++";
            case "T pan Millieu" : return idTpanMillieu + zeros(String.valueOf(vTpanMillieu)) + vTpanMillieu + "++";
        }
        return null;
    }

    private void changerTrame(String job)
    {
        int panMove;
        if(job.equals("PLUS"))
            panMove = 50;
        else
            panMove = -50;
        switch (spinner.getSelectedItem().toString())
        {
            case "Panneau 1" : vpan1 += panMove;
                if(vpan1 < 0)
                    vpan1 = 0;
                textValue.setText(String.valueOf(vpan1));
                break;
            case "Panneau 2" : vpan2 += panMove;
                if(vpan2 < 0)
                    vpan2 = 0;
                textValue.setText(String.valueOf(vpan2));
                break;
            case "Panneau 3" : vpan3 += panMove;
                if(vpan3 < 0)
                    vpan3 = 0;
                textValue.setText(String.valueOf(vpan3));
                break;
            case "Panneau 4" : vpan4 += panMove;
                if(vpan4 < 0)
                    vpan4 = 0;
                textValue.setText(String.valueOf(vpan4));
                break;
            case "Panneau 5" : vpan5 += panMove;
                if(vpan5 < 0)
                    vpan5 = 0;
                textValue.setText(String.valueOf(vpan5));
                break;
            case "Panneau Global" : vpanGlobal += panMove;
                if(vpanGlobal < 0)
                    vpanGlobal = 0;
                textValue.setText(String.valueOf(vpanGlobal));
                break;
            case "T pan Avant" : vTpanAvant += panMove;
                if(vTpanAvant < 0)
                    vTpanAvant = 0;
                textValue.setText(String.valueOf(vTpanAvant));
                break;
            case "T pan Arrière" : vTpanArrière += panMove;
                if(vTpanArrière < 0)
                    vTpanArrière = 0;
                textValue.setText(String.valueOf(vTpanArrière));
                break;
            case "T pan Millieu" : vTpanMillieu += panMove;
                if(vTpanMillieu < 0)
                    vTpanMillieu = 0;
                textValue.setText(String.valueOf(vTpanMillieu));
                break;
        }
    }
}

//UUID 35ab00d3-22c4-41a0-9aba-bf6ad3271bce

//UUID Blueterm 00001101-0000-1000-8000-00805F9B34FB

//UUID d23a5tse-5e4f-zerg-5hg8-5g54g7e5r1g4 Fait main.

//Such fantastic!!! Much wonderful!!!