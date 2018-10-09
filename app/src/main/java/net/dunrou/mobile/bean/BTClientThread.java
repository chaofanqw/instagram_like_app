package net.dunrou.mobile.bean;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import net.dunrou.mobile.R;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BTClientThread extends Thread {
    private static String TAG = BTClientThread.class.getSimpleName();

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private Fragment mFragment;
    private Context mContext;

    private String image_path;

    private UUID MY_UUID = UUID.fromString("0811c45b-e99b-49dd-a6ac-dc5e262e254d");
//    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public BTClientThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Fragment fragment, Context context) {
        BluetoothSocket tmp = null;
        this.mDevice = device;
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mFragment = fragment;
        this.mContext = context;

        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();

        try {
            Log.d(TAG, "socket ready to connect: " + mSocket);
            mSocket.connect();
            Log.d(TAG, "socket connected: " + mSocket);

            MyBluetoothService.ConnectedThread connectedThread = new MyBluetoothService.ConnectedThread(mSocket, mContext);
            Log.d(TAG, "bt connect status: " + connectedThread.getIsConnected());
            if (connectedThread.getIsConnected())
                connectedThread.write(image_path);

        } catch (IOException e) {
//            e.printStackTrace();
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }

//        manageMyConnectedSocket(mSocket);

    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog((Activity) this.mContext, R.style
                .transparentFrameWindowStyle,
                listener, names);

        if (!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage_path(String path) {
        this.image_path = path;
    }
}
