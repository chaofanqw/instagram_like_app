package net.dunrou.mobile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.zomato.photofilters.SampleFilters;

import net.dunrou.mobile.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoFilterActivity extends AppCompatActivity {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

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
    @BindView(R.id.contrast_visibility)
    LinearLayout contrastVisibility;
    @BindView(R.id.contrast_progress)
    SeekBar contrastProgress;

    String imageFile;

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
    }




}
