package com.example.thomas.servbluetooth;

/**
 * Created by Thomas on 28/01/2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class AcceptThread extends Thread {


    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothServerSocket mServerSocket;

    private BluetoothSocket mBluetoothSocket = null;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final Handler mHandler;

    public AcceptThread(Handler handler) {
        Log.d("AcceptThread", "Construction du Thread");
        mHandler = handler;
        try {
            mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("ServBluetooth", MY_UUID);
        } catch (IOException e) {
        }

        Log.d("AcceptThread", "Construction du Thread done");
    }

    public void run() {

        Log.d("AcceptThread", "start run de l'infini");
        while (true) {
            try {
                mBluetoothSocket = mServerSocket.accept();
                manageConnectedSocket();
                mServerSocket.close();
                break;
            } catch (IOException e1) {
                if (mBluetoothSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }

        Log.d("AcceptThread", "Fin du run de l'infini");
    }

    private void manageConnectedSocket() {
        Log.d("AcceptThread", "Manage du servSocket");
        ConnectionThread conn = new ConnectionThread(mBluetoothSocket, mHandler);
        mHandler.obtainMessage(MainActivity.SOCKET_CONNECTED, conn).sendToTarget();
        conn.start();
        Log.d("AcceptThread", "Manage du servSocket Done");
    }

    public void cancel() {
        try {
            if (null != mServerSocket) {

                Log.d("AcceptThread", "Fermeture du servSocket");
                mServerSocket.close();
            }
        } catch (IOException e) {
        }
    }
}
