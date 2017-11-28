package com.SSUAndroidProject.fairy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.SSUAndroidProject.fairy.Fragment.Add_Review.Crop;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CreateUser extends AppCompatActivity {
    final int PICK_PHOTO = 1;
    final int CROP_PHOTO = 2;

    private SubmitButton btn;
    private UserInfo info;
    private ImageView profileImage;
    private ImageButton add_profile;
    private EditText signinedit;
    private EditText signinedit3;
    private EditText signinedit4;
    private Bitmap finalbitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        signinedit = (EditText) findViewById(R.id.signinedit);
        signinedit3 = (EditText) findViewById(R.id.signinedit3);
        signinedit4 = (EditText) findViewById(R.id.signinedit4);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        add_profile=(ImageButton)findViewById(R.id.profile_image_add);
        signinedit.setSelection(signinedit.length());
        signinedit3.setSelection(signinedit3.length());
        signinedit4.setSelection(signinedit4.length());
        btn = (SubmitButton) findViewById(R.id.btn);

        ImageButton backbutton = (ImageButton)findViewById(R.id.backbutton);
        BusProvider.getInstance().register(this);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String filename = "Tempa.png";
                    FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
                    finalbitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    stream.close();
                    finalbitmap.recycle();
                    setResult(RESULT_OK);

                    info = new UserInfo(signinedit3.getText().toString(),
                            signinedit.getText().toString(),
                            signinedit4.getText().toString());
                    BusProvider.getInstance().post(new PushEvent(info));
                    btn.reset();

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info = new UserInfo("11","11","11");
                BusProvider.getInstance().post(new PushEvent(info));
                btn.reset();
                finish();
            }
        });
    }

    public void Add_Profile(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri galleryPictureUri = data.getData();
            Intent intent = new Intent(this,Crop.class);
            intent.putExtra("Uri",galleryPictureUri);
            startActivityForResult(intent,CROP_PHOTO);
        }
        else if(requestCode == CROP_PHOTO && resultCode == Activity.RESULT_OK){
            String filename = "Temp.png";
            try {
                FileInputStream TempStream = this.openFileInput(filename);
                finalbitmap = BitmapFactory.decodeStream(TempStream);
                TempStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            profileImage.setImageBitmap(finalbitmap);
            add_profile.setAlpha(65);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
