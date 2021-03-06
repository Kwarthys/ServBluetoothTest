package com.example.thomas.servbluetooth;

/**
 * Created by Thomas on 28/01/2015.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ConnectionThread extends Thread {

    BluetoothSocket mBluetoothSocket;

    private final Handler mHandler;

    private InputStream mInStream;

    private OutputStream mOutStream;

    ConnectionThread(BluetoothSocket socket, Handler handler) {
        super();
        mBluetoothSocket = socket;
        mHandler = handler;
        try {
            mInStream = mBluetoothSocket.getInputStream();
            mOutStream = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        Log.d("ConnecThread", "debut du run de l'infini CoTrd");
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(mInStream, "US-ASCII"));
                char[] data = new char[8];
                reader.read(data);

                String value = "";
                for (char c : data) {
                    if (c == 0) {
                        value += ".";
                    } else {
                        value += c;
                    }
                }

                Log.d("AVEC CONVERSION", value);

                mHandler.obtainMessage(MainActivity.DATA_RECEIVED, value).sendToTarget();

                Log.d("ConnecThread", "Handler mis a jours avec " + value);

            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mOutStream.write(bytes);

            Log.d("ConnecThread", "Message partie du CoTrd");
        } catch (IOException e) {
        }
    }
}

