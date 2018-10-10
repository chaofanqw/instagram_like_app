package net.dunrou.mobile.bean;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.lzy.imagepicker.bean.ImageItem;

import net.dunrou.mobile.base.message.DiscoverMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MyBluetoothService {
    private static final String TAG = MyBluetoothService.class.getSimpleName();

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private DataInputStream mInStream;
        private DataOutputStream mOutStream;
        private ArrayList<ImageItem> mReceiveImages;
        private Boolean isConnected = false;

        private Boolean keepRunning = true;

        private Context mContext;

        public ConnectedThread(BluetoothSocket mSocket, Context context) {
            this.mSocket = mSocket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;
            mContext = context;

            try {
                tmpIn = new DataInputStream(mSocket.getInputStream());
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                Log.d(TAG, "bt output exception");
                EventBus.getDefault().post(new DiscoverMessage.BTConnectionLostEvent());
            }

            try {
                tmpOut = new DataOutputStream(mSocket.getOutputStream());
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

                long len = 0;
                try {
                    len = mInStream.readLong();

                    System.out.println("len = " + len);
                    byte[] bytes = new byte[(int) len];
                    mInStream.readFully(bytes);

                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/myIns/bt_images/");
                    dir.mkdirs();

                    String fileName = String.format("%d.png", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);

                    Log.d(TAG, "image path: " + dir + fileName);

                    FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                    fileOutputStream.write(bytes);

                    EventBus.getDefault().post(new DiscoverMessage.BTReceiveImagesEvent(dir + "/" + fileName));
//                    keepRunning = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));
//                // readLine() read and delete one line
//                try {
//                    String received_path = br.readLine();
//                    Log.d(TAG, "received path: " + received_path);
//                    EventBus.getDefault().post(new DiscoverMessage.BTReceiveImagesEvent(received_path));
//                    keepRunning = false;
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


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

            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bout);
            long len = bout.size();
            Log.i("sendImgMsg", "len: "+len);
            try {
                mOutStream.writeLong(len);
                mOutStream.write(bout.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }


//            StringBuffer sb = new StringBuffer();
//            sb.append(image_path);
//            sb.append("\n");
//            if (mOutStream != null) {
//                try {
//                    mOutStream.write(sb.toString().getBytes());
//                    mOutStream.flush();
//                    Log.d(TAG,"@ send data " + sb.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.d(TAG,"@ Client sending fail");
//                }
//            }
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
