package net.dunrou.mobile.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import net.dunrou.mobile.base.message.UploadDatabaseMessage;
import net.dunrou.mobile.base.message.UploadMessage;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.bean.FileProviderUtils;
import net.dunrou.mobile.bean.GlideImageLoader;
import net.dunrou.mobile.bean.ImagePickerAdapter;
import net.dunrou.mobile.bean.SelectDialog;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dunrou.mobile.R;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;
import net.dunrou.mobile.network.firebaseNetwork.UploadImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

/**
 * ================================================
 * 作    者：ikkong （ikkong@163.com），修改 jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：微信图片选择的Adapter, 感谢 ikkong 的提交
 * ================================================
 */
public class WxDemoActivity extends AppCompatActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int REQUEST_FILTER = 102;
    public static final int REQUEST_LOCATION = 103;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 8;               //允许选择图片最大数
    private MaterialDialog materialDialog;
    private MessageFormat messageFormat;
    private int uploadNumber;
    private ArrayList<String> paths = new ArrayList<>();
    private Location location = null;

    private SharedPreferences userInfo;

    @BindView(R.id.location)
    TextView locationView;
    @BindView(R.id.information_text)
    EditText informationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxdemo);
        ButterKnife.bind(this);
        userInfo = getSharedPreferences("UserInfo", MODE_PRIVATE);
        EventBus.getDefault().register(this);

        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
//        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        informationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                publishImages();
                return false;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("Take Photo");
                names.add("Choose from Gallery");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                /**
                                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                                 *
                                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                                 *
                                 * 如果实在有所需要，请直接下载源码引用。
                                 */
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(WxDemoActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(WxDemoActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }

                    }
                }, names);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    Log.d("result", "onActivityResult: "+images.size());
                    Intent intent = new Intent(this, PhotoFilterActivity.class);
                    intent.putExtra("photo", images.get(0).path);
                    startActivityForResult(intent, REQUEST_FILTER);
//                    selImageList.addAll(images);
//                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    Log.d("result", "onActivityResult: "+images.size());
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == PhotoFilterActivity.FILTER_DONE){
            if (data != null && requestCode == REQUEST_FILTER){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    Log.d("result", "onActivityResult: "+images.size());
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
        Intent data = new Intent();
        data.putExtra(MainActivity.PHOTO_INFO, true);
        setResult(RESULT_OK, data);
        finish();
    }

    @OnClick(R.id.btn_publish)
    public void publishImages(){
        if(selImageList != null && selImageList.size() != 0){
            for(ImageItem imageItem: selImageList){
                new UploadImage().upload(FileProviderUtils.uriFromFile(this, new File(imageItem.path)));
            }

            uploadNumber = 0;
            messageFormat = new MessageFormat("Uploading {0} of {1} images.");
            materialDialog = new MaterialDialog.Builder(this)
                                .title("Uploading Images")
                                .content(messageFormat.format(new Object[]{uploadNumber,selImageList.size()}))
                                .progress(true, 0)
                                .show();
        }else{
            Toast.makeText(this, "Please select at least one image.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_back)
    public void onBack(){
        this.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadMessage(UploadMessage uploadMessage){
        if (uploadMessage.isSuccess()) {
            uploadNumber++;
            paths.add(uploadMessage.getPath().toString());
            if(uploadNumber == selImageList.size()){
//                materialDialog.hide();
//                this.onBackPressed();
                materialDialog.setContent("Uploading the message.");
                publishMessage();
            }else {
                materialDialog.setContent(messageFormat.format(new Object[]{uploadNumber, selImageList.size()}));
            }
        }
        else {
            paths = new ArrayList<>();
            materialDialog.dismiss();
            Toast.makeText(this, "Something wrong when uploading", Toast.LENGTH_SHORT).show();
        }
    }

    private void publishMessage(){
        String information = informationView.getText().toString();
        Date date = Calendar.getInstance().getTime();
        String username = userInfo.getString("username", "1");
        FirebaseEventPost firebaseEventPost = new FirebaseEventPost(null, username, information, paths, location, date);
        new FirebaseUtil().EventPostInsert(firebaseEventPost);
    }

    @OnClick(R.id.geo_locate)
    public void getLocation(){
        MPermissions.requestPermissions(this, REQUEST_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUEST_LOCATION)
    public void requestSdcardSuccess() {
        SmartLocation.with(this)
                .location(new LocationGooglePlayServicesProvider())
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location mlocation) {
                        location = mlocation;
                        locationView.setText("Longitude:\t" + mlocation.getLongitude()+" Latitude:\t"+mlocation.getLatitude());
                    }});
    }

    @PermissionDenied(REQUEST_LOCATION)
    public void requestSdcardFailed()
    {
        Toast.makeText(this, "Cannot get access to location!", Toast.LENGTH_SHORT).show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadDatabaseMessage(UploadDatabaseMessage uploadDatabaseMessage){
        if(uploadDatabaseMessage.isSuccess()){
            materialDialog.dismiss();
            this.onBackPressed();
        }else{
            materialDialog.dismiss();
            Toast.makeText(this, "Some thing wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
