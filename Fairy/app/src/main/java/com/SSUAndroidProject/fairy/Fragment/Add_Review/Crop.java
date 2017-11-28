package com.SSUAndroidProject.fairy.Fragment.Add_Review;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.SSUAndroidProject.fairy.BusProvider;
import com.SSUAndroidProject.fairy.Fragment.Add_Review.Crop_Image_Library.CropView;
import com.SSUAndroidProject.fairy.R;
import com.melnykov.fab.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;

public class Crop extends AppCompatActivity {

    private Uri galleryPictureUri;
    private FloatingActionButton Rotate_Button ;
    private FloatingActionButton Crop_Button ;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Crop_Button= (FloatingActionButton)findViewById(R.id.Crop_fab);
        Rotate_Button = (FloatingActionButton)findViewById(R.id.Rotate_fab);
        final CropView cropView = (CropView)findViewById(R.id.Crop_CropView);
        BusProvider.getInstance().register(this);
        cropView.setViewportRatio(1);
        cropView.setViewportOverlayPadding(70);

        Intent intent = getIntent();
        galleryPictureUri = intent.getParcelableExtra("Uri");
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryPictureUri);
        }catch (IOException e){

        }
        cropView.setImageBitmap(bitmap);

        Rotate_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                bitmap = CropView.imgRotate(bitmap);
                cropView.setImageBitmap(bitmap);
            }

        });

        Crop_Button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                bitmap = cropView.crop();
                try {
                    String filename = "Temp.png";
                    FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    bitmap.recycle();
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
