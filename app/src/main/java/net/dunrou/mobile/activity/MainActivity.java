package net.dunrou.mobile.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import net.dunrou.mobile.R;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.bean.DataGenerator;
import net.dunrou.mobile.bean.IGetLocation;
import net.dunrou.mobile.fragment.DiscoverFragment;
import net.dunrou.mobile.fragment.HomeFragment;
import net.dunrou.mobile.network.HttpResult;
import net.dunrou.mobile.network.InsNetwork;
import net.dunrou.mobile.network.InsService;
import net.dunrou.mobile.network.RetrofitUtil;


import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainActivity extends BaseActivity implements View.OnClickListener, IGetLocation {
    public static final int PHOTO_TAKE = 10001;
    public static final String PHOTO_INFO = "info";

    private TabLayout mTabLayout;
    private Fragment []mFragmensts;

    private final static String TAG = MainActivity.class.getSimpleName();
    public static String CURRENT_USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CURRENT_USERID = getIntent().getStringExtra("CURRENT_USERID");
        mFragmensts = DataGenerator.getFragments(CURRENT_USERID);
        initView();
        Log.d(TAG, "CURRENT_USERID: " + CURRENT_USERID);



//        TextView test = findViewById(R.id.test);
//        test.setOnClickListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.bottom_tab_layout);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabItemSelected(tab.getPosition());
                // Tab 选中之后，改变各个Tab的状态
                for (int i=0;i<mTabLayout.getTabCount();i++){
                    View view = mTabLayout.getTabAt(i).getCustomView();
                    ImageView icon = (ImageView) view.findViewById(R.id.tab_content_image);
                    TextView text = (TextView) view.findViewById(R.id.tab_content_text);
                    if(i == tab.getPosition()){ // 选中状态
                        icon.setImageResource(DataGenerator.mTabResPressed[i]);
                        text.setTextColor(getResources().getColor(R.color.blueLight));
                    }else{// 未选中状态
                        icon.setImageResource(DataGenerator.mTabRes[i]);
                        text.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 提供自定义的布局添加Tab
        for(int i=0;i<5;i++){
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(DataGenerator.getTabView(this,i)));
        }

    }

    private void onTabItemSelected(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = mFragmensts[0];
                break;
            case 1:
                fragment = mFragmensts[1];
                break;
            case 2:
                fragment = mFragmensts[2];
                break;
            case 3:
                fragment = mFragmensts[3];
                break;
            case 4:
                fragment = mFragmensts[4];
                break;
        }
        if(fragment!=null) {
            openFragment(R.id.home_container,fragment);
        }else{
            if(position == 2){
                Intent intent = new Intent(this, WxDemoActivity.class);
                startActivityForResult(intent, PHOTO_TAKE);
            }
        }
    }

    public void onPhotoSuccess(){
        mTabLayout.getTabAt(0).select();
    }


    private void getMovie(){
        InsNetwork insNetwork = InsService.getInstance().getInsNetwork();

        Observer<HttpResult<String>> deal = new Observer<HttpResult<String>>() {

            @Override
            public void onError(Throwable e) {
                Log.d("result", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "Get Top Movie Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribe(Disposable d) {
                Toast.makeText(MainActivity.this, "Get Top Movie begin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(HttpResult<String> movieEntity) {
                Log.d("result", "onNext: " + movieEntity.getData());
            }
        };

        RetrofitUtil.bind(insNetwork.getTopMovie(0, 10), deal);
    }

    @Override
    public void onClick(View view) {
//        Intent intent = new Intent(this, WxDemoActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == PHOTO_TAKE){
                if(data.getBooleanExtra(PHOTO_INFO, false)){
                    onPhotoSuccess();
                    getLocation();
                }
            }
        }
    }

    @Override
    public void getLocation(){
        MPermissions.requestPermissions(this, 100, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(100)
    public void requestSdcardSuccess() {
        SmartLocation.with(this)
                .location(new LocationGooglePlayServicesProvider())
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location mlocation) {
                        ((HomeFragment) mFragmensts[0]).initData(mlocation);
                    }});
    }

    @PermissionDenied(100)
    public void requestSdcardFailed()
    {
        Toast.makeText(this, "Cannot get access to location!", Toast.LENGTH_SHORT).show();
    }

}
