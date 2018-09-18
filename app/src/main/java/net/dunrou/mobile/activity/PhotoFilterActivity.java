package net.dunrou.mobile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubfilter;

import net.dunrou.mobile.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoFilterActivity extends AppCompatActivity {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    public static final int FILTER_DONE = 1006;

    @BindView(R.id.place_holder_imageview)
    ImageView placeHolderImageview;
    @BindView(R.id.filters)
    LinearLayout filters;
    @BindView(R.id.brightness)
    ImageView brightness;
    @BindView(R.id.contrast)
    ImageView contrast;
    @BindView(R.id.blue_mess)
    ImageView blueMess;
    @BindView(R.id.lime_shutter)
    ImageView limeShutter;
    @BindView(R.id.night_whisper)
    ImageView nightWhisper;
    @BindView(R.id.brightness_visibility)
    LinearLayout brightnessVisibility;
    @BindView(R.id.brightness_progress)
    SeekBar brightProgress;
    @BindView(R.id.brightness_done)
    TextView brightnessDone;
    @BindView(R.id.contrast_visibility)
    LinearLayout contrastVisibility;
    @BindView(R.id.contrast_progress)
    SeekBar contrastProgress;
    @BindView(R.id.contrast_done)
    TextView contrastDone;


    String imageFile;
    int brightnessDegree = 30;
    float contrastDegree = 1.0f;
    int filterNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_filter);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        imageFile = intent.getStringExtra("photo");

        Bitmap image = BitmapFactory.decodeFile(imageFile);
        placeHolderImageview.setImageBitmap(image);

        image = BitmapFactory.decodeFile(imageFile);
        image = image.copy(image.getConfig(), true);
        blueMess.setImageBitmap(SampleFilters.getBlueMessFilter().processFilter(image));

        image = BitmapFactory.decodeFile(imageFile);
        image = image.copy(image.getConfig(), true);
        limeShutter.setImageBitmap(SampleFilters.getLimeStutterFilter().processFilter(image));

        image = BitmapFactory.decodeFile(imageFile);
        image = image.copy(image.getConfig(), true);
        nightWhisper.setImageBitmap(SampleFilters.getNightWhisperFilter().processFilter(image));

        brightProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessDegree = i;
                placeHolderImageview.setImageBitmap(generateImage());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        contrastProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                contrastDegree = (float) (1.0f + 0.01 * i);
                placeHolderImageview.setImageBitmap(generateImage());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private Bitmap generateImage(){
        Bitmap image = BitmapFactory.decodeFile(imageFile);
        image = image.copy(image.getConfig(), true);

        Filter filter = new Filter();
        filter.addSubFilter(new BrightnessSubfilter(brightnessDegree));
        filter.addSubFilter(new ContrastSubfilter(contrastDegree));
        image = filter.processFilter(image);

        image = applyFilter(image);

        return image;
    }

    private Bitmap applyFilter(Bitmap bitmap){
        switch(filterNumber){
            case 0:
                break;
            case 1:
                return SampleFilters.getBlueMessFilter().processFilter(bitmap);
            case 2:
                return SampleFilters.getLimeStutterFilter().processFilter(bitmap);
            case 3:
                return SampleFilters.getNightWhisperFilter().processFilter(bitmap);
        }
        return bitmap;
    }

    @OnClick(R.id.brightness)
    public void setBrightness(){
        filters.setVisibility(View.GONE);
        brightnessVisibility.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.brightness_done)
    public void setBrightnessDone(){
        filters.setVisibility(View.VISIBLE);
        brightnessVisibility.setVisibility(View.GONE);
    }

    @OnClick(R.id.contrast)
    public void setContrast(){
        filters.setVisibility(View.GONE);
        contrastVisibility.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.contrast_done)
    public void setContrastDone(){
        filters.setVisibility(View.VISIBLE);
        contrastVisibility.setVisibility(View.GONE);
    }


    @OnClick(R.id.blue_mess)
    public void applyBlueMess(){
        filterNumber = 1;
        placeHolderImageview.setImageBitmap(generateImage());
    }


    @OnClick(R.id.lime_shutter)
    public void applyLimeShutter(){
        filterNumber = 2;
        placeHolderImageview.setImageBitmap(generateImage());
    }

    @OnClick(R.id.night_whisper)
    public void applyNightWhisper(){
        filterNumber = 3;
        placeHolderImageview.setImageBitmap(generateImage());
    }

    @OnClick(R.id.btn_back)
    public void cancel(){
        this.onBackPressed();
    }


    @OnClick(R.id.btn_ok)
    public void complete(){
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/images";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);

        File file = new File(dir, "sketchpad" + dateFormat.format(new Date(System.currentTimeMillis())) + ".png");
        FileOutputStream fOut = null;

        try {
            fOut = new FileOutputStream(file);
            generateImage().compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            ArrayList<ImageItem> list = new ArrayList<ImageItem>();
            ImageItem newItem = new ImageItem();
            newItem.path = file.getAbsolutePath();
            list.add(newItem);

            Intent intent  = new Intent();
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, list);
            setResult(FILTER_DONE, intent);
            finish();
        }
            catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something wrong when store image", Toast.LENGTH_SHORT).show();
        }

    }




}
