package net.dunrou.mobile.bean;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.lzy.imagepicker.bean.ImageItem;

import net.dunrou.mobile.base.message.DiscoverMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;


public class MyBluetoothService {
    private static final String TAG = MyBluetoothService.class.getSimpleName();

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private InputStream mInStream;
        private OutputStream mOutStream;
        private ArrayList<ImageItem> mReceiveImages;
        private Boolean isConnected = false;

        private Boolean keepRunning = true;

        private Context mContext;

        public ConnectedThread(BluetoothSocket mSocket, Context context) {
            this.mSocket = mSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mContext = context;

            try {
                tmpIn = mSocket.getInputStream();
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                Log.d(TAG, "bt output exception");
                EventBus.getDefault().post(new DiscoverMessage.BTConnectionLostEvent());
            }

            try {
                tmpOut = mSocket.getOutputStream();
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                Log.d(TAG, "bt input exception");
                EventBus.getDefault().post(new DiscoverMessage.BTConnectionLostEvent());
            }
            if (isConnected) {
                mInStream = tmpIn;
                mOutStream = tmpOut;

                Log.d(TAG, "input and output stream got");
            }
        }

        public void run() {
            mReceiveImages = new ArrayList<ImageItem>();
            int numBytes;
            byte[] mBuffer = new byte[8192];

            while (keepRunning && mInStream != null) {

                BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));
                // readLine() read and delete one line
                try {
                    String received_path = br.readLine();
                    Log.d(TAG, "received path: " + received_path);
                    EventBus.getDefault().post(new DiscoverMessage.BTReceiveImagesEvent(received_path));
                    keepRunning = false;


                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(Thread.currentThread().isInterrupted())
                {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "quit from connection");
                    keepRunning = false;
                    break;
                }

            }
        }

        public void write(String image_path) {
            Log.d(TAG, "write using BT called, received image_path: " + String.valueOf(image_path));
            Log.d(TAG, "mOutStream: " + mOutStream);

            StringBuffer sb = new StringBuffer();
            sb.append(image_path);
            sb.append("\n");
            if (mOutStream != null) {
                try {
                    mOutStream.write(sb.toString().getBytes());
                    mOutStream.flush();
                    Log.d(TAG,"@ send data " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"@ Client sending fail");
                }
            }
            Log.d(TAG, "sent successfully!!!");
        }

        public void  cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Boolean getIsConnected() {
            return isConnected;
        }
    }


}
