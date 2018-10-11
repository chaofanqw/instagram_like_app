package net.dunrou.mobile.bean;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.WxDemoActivity_BT;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BTDeviceViewHolder> {

    private static String TAG = BluetoothDeviceAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<BluetoothDevice> mBluetoothDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private Fragment mFragment;
    private BTClientThread mBTClientThread;


    public BluetoothDeviceAdapter(Context context, Fragment fragment) {
        mFragment = fragment;
        mContext = context;
        mBluetoothDevices = new ArrayList<>();
    }

    @Override
    public BTDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BTDeviceViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.discover_bt_item, null));
    }

    @Override
    public void onBindViewHolder(BTDeviceViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
        initializeHolderView(holder, bluetoothDevice);
        initializeConnectLogic(holder, bluetoothDevice);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mBluetoothDevices.size());
        return mBluetoothDevices.size();
    }

    public void setBluetoothDevices(ArrayList<BluetoothDevice> bluetoothDevices) {
        this.mBluetoothDevices = bluetoothDevices;
        Log.d(TAG, "setBluetoothDevices called, devices number: " + this.mBluetoothDevices.size());
        notifyDataSetChanged();
    }

    public void initializeConnectLogic(final BTDeviceViewHolder holder, final BluetoothDevice device) {
        holder.mBt_connect_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Click connect button on : " + holder.mBt_device_name_TV.getText());
                mBTClientThread = new BTClientThread(device, mBluetoothAdapter, mFragment, mContext);

                Intent intent = new Intent(mContext, WxDemoActivity_BT.class);
                mContext.startActivity(intent);
            }
        });
    }

    public void sendImages (String path) {
        mBTClientThread.setImage_path(path);
        mBTClientThread.start();
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public static class BTDeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView mBt_device_name_TV;
        private final TextView mBt_device_MAC_TV;
        private final Button mBt_connect_BT;


        private BTDeviceViewHolder(View view) {
            super(view);
            mBt_device_name_TV = (TextView) view.findViewById(R.id.bt_device_name_TV);
            mBt_device_MAC_TV = (TextView) view.findViewById(R.id.bt_device_MAC_TV);
            mBt_connect_BT = view.findViewById(R.id.bt_connect_BT);
        }
    }

    public void initializeHolderView(BTDeviceViewHolder holder, BluetoothDevice bluetoothDevice) {
        String name;
        try {
            String[] names = bluetoothDevice.getName().split(",");
            name = names[names.length-1];
            Log.d(TAG, "test name: " + bluetoothDevice.getName().split(",")[0]);
            Log.d(TAG, "test name: " + bluetoothDevice.getName().split(",")[1]);
        } catch (NullPointerException e) {
            name = "Unknown";
        }

        holder.mBt_device_name_TV.setText(name);

        holder.mBt_device_MAC_TV.setText(bluetoothDevice.getAddress());
    }
}
