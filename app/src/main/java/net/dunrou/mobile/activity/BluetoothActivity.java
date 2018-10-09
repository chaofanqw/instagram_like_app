//package net.dunrou.mobile.activity;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHeadset;
//import android.bluetooth.BluetoothProfile;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.Toast;
//
//import net.dunrou.mobile.R;
//import net.dunrou.mobile.bean.BaseActivity;
//
//import java.util.Set;
//
//public class BluetoothActivity extends BaseActivity {
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    private BluetoothAdapter mBluetoothAdapter;
//    private Set<BluetoothDevice> pairedDevices;
//    private BluetoothHeadset mBluetoothHeadset;
//
//    private int DISCOVERABLE_TIME_SHORT = 300;
//
//    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
//        @Override
//        public void onServiceConnected(int profile, BluetoothProfile proxy) {
//            if (profile == BluetoothHeadset.HEADSET) {
//                mBluetoothHeadset = (BluetoothHeadset) proxy;
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(int profile) {
//            if (profile == BluetoothHeadset.HEADSET) {
//                mBluetoothHeadset = null;
//            }
//        }
//    };
//
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress();
//            }
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        setupBT();
//        setupProfile();
//        discoverDevices();
//        setupDiscoverable(DISCOVERABLE_TIME_SHORT);
//        getPairedDevices();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        stopDiscoverDevices();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == DISCOVERABLE_TIME_SHORT) {
////            TODO user enable discoverable mode
//        }
//    }
//
//    public void setupBT() {
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if(mBluetoothAdapter == null) {
//            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
//        }
//
//        if(mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
//
//    public void getPairedDevices() {
//        pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress();
//            }
//        }
//    }
//
//    public void setupProfile() {
//        mBluetoothAdapter.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.HEADSET);
//        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
//    }
//
//    public void discoverDevices() {
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
//
//    }
//
//    public void stopDiscoverDevices() {
//        unregisterReceiver(mReceiver);
//    }
//
//    public void setupDiscoverable(int discoverTime) {
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverTime);
//        startActivity(discoverableIntent);
//    }
//
//}
