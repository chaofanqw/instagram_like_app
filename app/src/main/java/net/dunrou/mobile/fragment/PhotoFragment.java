package net.dunrou.mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.activity.WxDemoActivity;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.bean.BaseFragment;
import net.dunrou.mobile.bean.PicassoImageLoader;

import java.util.ArrayList;

/**
 * Created by Stephen on 2018/9/13.
 */

public class PhotoFragment extends BaseFragment{
    public static final int IMAGE_PICKER = 1;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
//        imagePicker.setShowCamera(true);  //显示拍照按钮
//        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
//        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
//        imagePicker.setMultiMode(false);    //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
//        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
//        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

//        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
//        startActivityForResult(intent, IMAGE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).onPhotoSuccess();
        } else {
            Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onClick(View view) {
//        Intent intent = new Intent(getActivity(), WxDemoActivity.class);
////        startActivityForResult(intent, IMAGE_PICKER);
//        startActivity(intent);
//    }
}
