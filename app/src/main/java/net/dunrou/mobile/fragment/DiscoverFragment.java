package net.dunrou.mobile.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.bean.BTServerThread;
import net.dunrou.mobile.bean.BluetoothDeviceAdapter;
import net.dunrou.mobile.bean.DiscoverUserAdapter;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
//import net.dunrou.mobile.activity.SearchableActivity;

/**
 * Created by yvette on 2018/9/18.
 */

public class DiscoverFragment extends Fragment implements SearchView.OnQueryTextListener {
    private transient final static String TAG = DiscoverFragment.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    private View mView;

    private SearchManager mSearchManager;
    private DiscoverUserAdapter mDiscoverUserAdapter;
    private Context mContext;

    private SearchView mSearch_SV;
    private RecyclerView mResults_RV;
    private Button mSearchCancel_BT;
    private TextView mSearchUser_TV;

    private TabLayout mDiscover_TL;
    private TabLayout.Tab tab_top;
    private TabLayout.Tab tab_commonFriend;
    private TabLayout.Tab tab_nearby;

    private Button mbt_discoverable_BT;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<BluetoothDevice> mDiscoverableDevices = new ArrayList<>();
    private BluetoothHeadset mBluetoothHeadset;

    private BluetoothDeviceAdapter  mBluetoothDeviceAdapter;

    private ArrayList<String> mPermissions_list;
    private String[] mPermissions_array;

    public static int DISCOVERABLE_TIME_SHORT = 300;
    public static int DISCOVERABLE_TIME_LONG = 900;

    public static final UUID MY_UUID = UUID.fromString("0811c45b-e99b-49dd-a6ac-dc5e262e254d");



    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothHeadset.HEADSET) {
                mBluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothHeadset.HEADSET) {
                mBluetoothHeadset = null;
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive called");
            Boolean isExisted = false;
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "get device: " + device.getName() + " " + device.getAddress() + " " + device.getUuids());
                try {
                    if(device.getName().split(",")[0].equals(MY_UUID.toString())) {
                        for(BluetoothDevice d: mDiscoverableDevices) {
                            if (d.getAddress().equals(device.getAddress()))
                                isExisted = true;
                            break;
                        }
                        if(!isExisted)
                        {
                            mDiscoverableDevices.add(device);
                            mBluetoothDeviceAdapter.setBluetoothDevices(mDiscoverableDevices);
                        }
                    }
                } catch (NullPointerException e) {}

            }
        }
    };

    public DiscoverFragment() {
    }

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        mContext = this.getActivity();
        EventBus.getDefault().register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getAllPermissions();
        }

        setupBT();
        setupProfile();
        startDiscoverDevices();
        startDiscover();
        startBTServerThread();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
    }

    @Override
    public void onResume() {
        super.onResume();
        setupNearby();

//        Log.d(TAG, "onResume called");
//        switch(mDiscoverUserAdapter.getSuggestMode()) {
//            case DiscoverUserAdapter.TOP:
//                setupTop();
//                break;
//            case DiscoverUserAdapter.COMMON_FRIENDS:
//                setupPeople();
//                break;
//            case DiscoverUserAdapter.INRANGE:
//                setupNearby();
//                break;
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mDiscoverUserAdapter.getSuggestMode() == DiscoverUserAdapter.INRANGE)
//            stopDiscoverDevices();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBluetoothAdapter.disable();
//        stopDiscoverDevices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_discover, container, false);

        initializeTabLayout();
        initializeSearch();
        initializeAdapter();
        initializeRecycleLayout();

        return mView;
    }

    public void initializeTabLayout() {
        mDiscover_TL = mView.findViewById(R.id.discover_TL);
        tab_top= mDiscover_TL.getTabAt(0);
        tab_commonFriend = mDiscover_TL.getTabAt(1);
        tab_nearby = mDiscover_TL.getTabAt(2);
        tab_nearby.select();
        mDiscover_TL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        Log.d(TAG, "click top");
                        setupTop();
                        break;
                    case 1:
                        Log.d(TAG, "click people");
                        setupPeople();
                        break;
                    case 2:
                        Log.d(TAG, "click nearby");
                        setupNearby();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mbt_discoverable_BT = mView.findViewById(R.id.bt_discoverable_BT);
        mbt_discoverable_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDiscoverable(DISCOVERABLE_TIME_LONG);
            }
        });
    }

    public void setupTop() {
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mSearch_SV.clearFocus();
        mSearch_SV.setQuery("", false);
        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TOP);
        mSearchUser_TV.setVisibility(View.GONE);
        mbt_discoverable_BT.setVisibility(View.GONE);
    }

    public void setupPeople() {
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mSearch_SV.clearFocus();
        mSearch_SV.setQuery("", false);
        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.COMMON_FRIENDS);
        mSearchUser_TV.setVisibility(View.GONE);
        mbt_discoverable_BT.setVisibility(View.GONE);
    }

    public void setupNearby() {
        startDiscover();
        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.INRANGE);
        mResults_RV.setAdapter(mBluetoothDeviceAdapter);
        mbt_discoverable_BT.setVisibility(View.VISIBLE);
    }

    public void initializeSearch() {
        mSearch_SV = mView.findViewById(R.id.search_SV);
        mSearchCancel_BT = mView.findViewById(R.id.searchCancel_BT);
        mSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchUser_TV = mView.findViewById(R.id.searchUser_TV);

        mSearch_SV.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearch_SV.setIconifiedByDefault(false);

        mSearch_SV.setSubmitButtonEnabled(true);
        mSearch_SV.setOnQueryTextListener(this);

        mSearchCancel_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch_SV.clearFocus();
                mSearchUser_TV.setVisibility(View.GONE);

                mSearch_SV.setQuery("", false);

                if(tab_top.isSelected())
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TOP);
                else if(tab_commonFriend.isSelected())
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.COMMON_FRIENDS);
                else
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.INRANGE);
            }
        });
    }

    public void initializeAdapter() {
        mDiscoverUserAdapter = new DiscoverUserAdapter(mContext);
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(mContext, this);
        mBluetoothDeviceAdapter.setmBluetoothAdapter(mBluetoothAdapter);

    }

    public void initializeRecycleLayout() {
        mResults_RV = mView.findViewById(R.id.results_RV);
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mResults_RV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        Log.d(TAG, "receive query: " + query);
        searchUsers(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Log.d(TAG, "receive text change: " + newText);
        searchUsers(newText);
        return false;
    }


    public void searchUsers(String query) {
        mDiscoverUserAdapter.getSuggestedUsers_search().clear();
        mSearchUser_TV.setVisibility(View.VISIBLE);

        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.SEARCH);
        if(mDiscoverUserAdapter.getSuggestMode() == DiscoverUserAdapter.SEARCH)
            new FirebaseUtil().searchUser(query);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getAllPermissions() {
        mPermissions_list = new ArrayList<>();

        mPermissions_list.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        mPermissions_list.add(android.Manifest.permission.BLUETOOTH);
        mPermissions_list.add(android.Manifest.permission.BLUETOOTH_ADMIN);

        mPermissions_array = new String[mPermissions_list.size()];

        mPermissions_array = mPermissions_list.toArray(mPermissions_array);
        requestPermissions(mPermissions_array, 1);
    }

    public void setupBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.setName(MY_UUID.toString() + "," + mBluetoothAdapter.getName());


        if(mBluetoothAdapter == null) {
            Toast.makeText(mContext, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "bluetooth isEnable: " + mBluetoothAdapter.isEnabled());
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

//    public void setPairedDevices() {
//        pairedDevices = mBluetoothAdapter.getBondedDevices();
//        ArrayList<BluetoothDevice> deviceArrayList = new ArrayList<>(pairedDevices);
////        mBluetoothDeviceAdapter.setBluetoothDevices(deviceArrayList);
//    }

    public void setupProfile() {
        mBluetoothAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
    }

    public void startDiscoverDevices() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
        Log.d(TAG, "registerReceiver");

    }

    public void stopDiscoverDevices() {
        mContext.unregisterReceiver(mReceiver);
        Log.d(TAG, "unregisterReceiver");
    }

    public void setupDiscoverable(int discoverTime) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverTime);
        startActivity(discoverableIntent);
    }

    public void startDiscover() {
        mBluetoothAdapter.startDiscovery();
    }

    public void startBTServerThread() {
        BTServerThread btServerThread = new BTServerThread(mBluetoothAdapter, mContext);
        btServerThread.start();
    }

    public void sendImages (String path) {
        mBluetoothDeviceAdapter.sendImages(path);
    }

    public void showBTDialog(String path) {

        final Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(mContext).inflate(R.layout.discover_bt_dialog, null);

        final ImageView bt_image_IV = (ImageView) view.findViewById(R.id.bt_image_IV);

        File imgFile = new  File(path);
        Log.d(TAG, "test path!" + path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        bt_image_IV.setImageBitmap(myBitmap);

        Button bt_save_BT = (Button) view.findViewById(R.id.bt_save_BT);
        bt_save_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery((BitmapDrawable) bt_image_IV.getDrawable());
                settingsDialog.cancel();
            }
        });

        Button bt_ignore_BT = (Button) view.findViewById(R.id.bt_ignore_BT);
        bt_ignore_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.cancel();
            }
        });

        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    /**
     * save image to gallery
     */
    public void saveToGallery(BitmapDrawable data) {
        BitmapDrawable draw = data;
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/myIns");
        dir.mkdirs();

        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            // Save image to gallery
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    mContext.getContentResolver(),
                    bitmap,
                    fileName,
                    ""
            );

            Log.d(TAG, "saved: " + savedImageURL);

            Toast toast = Toast.makeText(mContext, "Image saved!", Toast.LENGTH_SHORT);
            toast.show();

            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationAddFail(DiscoverMessage.RelationAddFailEvent relationAddFailEvent) {
        Toast toast = Toast.makeText(mContext, "follow fail", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipAddedEvent(DiscoverMessage.RelationshipAddedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.addRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipChangedEvent(DiscoverMessage.RelationshipChangedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.updateRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipRemovedEvent(DiscoverMessage.RelationshipRemovedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.removeRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAddedEvent(DiscoverMessage.UserAddedEvent userAddedEvent) {
        mDiscoverUserAdapter.addUser(userAddedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserChangedEvent(DiscoverMessage.UserChangedEvent userChangedEvent) {
        mDiscoverUserAdapter.updateUser(userChangedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRemovedEvent(DiscoverMessage.UserRemovedEvent userRemovedEvent) {
        mDiscoverUserAdapter.removeUser(userRemovedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserSearchGetEvent(DiscoverMessage.UserSearchGetEvent userSearchGetEvent) {
        mSearchUser_TV.setVisibility(View.GONE);
        if(mDiscoverUserAdapter.getSuggestMode() == DiscoverUserAdapter.SEARCH)
            mDiscoverUserAdapter.userSearchGet(userSearchGetEvent.getSuggestedUsers());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBTSendImagesEvent(DiscoverMessage.BTSendImagesEvent btSendImagesEvent) {
        Log.d(TAG, "Eventbus receive BTSendImagesEvent");
        sendImages(btSendImagesEvent.getPath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBTReceivedImagesEvent(DiscoverMessage.BTReceiveImagesEvent btReceiveImagesEvent) {
        Log.d(TAG, "Eventbus receive BTSendImagesEvent");
        showBTDialog(btReceiveImagesEvent.getPath());
    }
}
