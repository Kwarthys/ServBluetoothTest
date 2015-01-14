package com.example.thomas.servbluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;

/*********************************
 * Created by Thomas on 14/01/2015.
**********************************/
public class TestThread extends Thread{

    private BluetoothServerSocket servSocket; //hair

    public TestThread(BluetoothServerSocket serv)
    {
        super();
        this.servSocket = serv;
    }

    public void run()
    {
        BluetoothSocket socket;

        while (true) {
            try {
                socket = servSocket.accept();
            } catch (IOException e) {
                break;
            }
            if (socket != null) {
                try {
                    servSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
