package net.dunrou.mobile.bean;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import net.dunrou.mobile.base.message.DiscoverMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

import static android.provider.Settings.NameValueTable.NAME;

public class BTServerThread extends Thread {
    private static String TAG = BTServerThread.class.getSimpleName();

    private Context mContext;

    private BluetoothServerSocket mServerSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private UUID MY_UUID = UUID.fromString("0811c45b-e99b-49dd-a6ac-dc5e262e254d");
    private Boolean keepRunning = true;
//    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BTServerThread(BluetoothAdapter mBluetoothAdapter, Context context) {
        mContext = context;
        this.mBluetoothAdapter = mBluetoothAdapter;
        BluetoothServerSocket tmp = null;
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;

        while (keepRunning) {
            try {
                socket = mServerSocket.accept();
                Log.d(TAG, "accept socket: " + socket);
                MyBluetoothService.ConnectedThread connectedThread = new MyBluetoothService.ConnectedThread(socket, mContext);
                if(connectedThread.getIsConnected())
                    connectedThread.start();
                else
                    EventBus.getDefault().post(new DiscoverMessage.BTConnectionLostEvent());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
                try {
                    mServerSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                BTServerThread btServerThread = new BTServerThread(mBluetoothAdapter, mContext);
                btServerThread.start();
                keepRunning = false;

            }

            if (socket != null) {
//                manageMyConnectedSocket(socket);
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
