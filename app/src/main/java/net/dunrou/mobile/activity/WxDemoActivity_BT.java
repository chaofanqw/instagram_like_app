package net.dunrou.mobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;
import net.dunrou.mobile.base.message.UploadMessage;
import net.dunrou.mobile.bean.FileProviderUtils;
import net.dunrou.mobile.bean.GlideImageLoader;
import net.dunrou.mobile.bean.ImagePickerAdapter;
import net.dunrou.mobile.bean.SelectDialog;
import net.dunrou.mobile.network.firebaseNetwork.UploadImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WxDemoActivity_BT extends AppCompatActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    public static String TAG = WxDemoActivity_BT.class.getSimpleName();

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int REQUEST_FILTER = 102;
    public static final int REQUEST_LOCATION = 103;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList;
    private int maxImgCount = 1;
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
        setContentView(R.layout.activity_wxdemo_bt);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initImagePicker();
        initWidget();
        Log.d("testing", "testing bt activity");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(true);
        imagePicker.setSaveRectangle(true);
//        imagePicker.setSelectLimit(maxImgCount);
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(1000);
        imagePicker.setOutPutY(1000);
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
                            case 0:
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent = new Intent(WxDemoActivity_BT.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                Intent intent1 = new Intent(WxDemoActivity_BT.this, ImageGridActivity.class);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }

                    }
                }, names);
                break;
            default:
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
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    Log.d("result", "onActivityResult: "+images.size());
                    Intent intent = new Intent(this, PhotoFilterActivity.class);
                    intent.putExtra("photo", images.get(0).path);
                    startActivityForResult(intent, REQUEST_FILTER);
//                    selImageList.addAll(images);
//                    adapter.setImage_path(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
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

    @OnClick(R.id.btn_send)
    public void publishImages(){
        if(selImageList != null && selImageList.size() != 0){
            for(ImageItem imageItem: selImageList){
                new UploadImage().upload(FileProviderUtils.uriFromFile(this, new File(imageItem.path)));
            }

            uploadNumber = 0;
            messageFormat = new MessageFormat("Sending {0} of {1} images.");
            materialDialog = new MaterialDialog.Builder(this)
                    .title("Sending Images")
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
                materialDialog.hide();
                materialDialog.setContent("sending the message.");
                EventBus.getDefault().post(new DiscoverMessage.BTSendImagesEvent(paths.get(0)));
                this.onBackPressed();
            }else {
//                materialDialog.setContent(messageFormat.format(new Object[]{uploadNumber, selImageList.size()}));
            }
        }
        else {
            paths = new ArrayList<>();
            materialDialog.dismiss();
            Toast.makeText(this, "Something wrong when uploading", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionLost(DiscoverMessage.BTConnectionLostEvent btConnectionLostEvent){
        Log.d(TAG, "connection lost");
        Toast.makeText(this, "connection lost", Toast.LENGTH_SHORT);
        this.onBackPressed();

    }
}
