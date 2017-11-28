package com.SSUAndroidProject.fairy.Fragment.Add_Review;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.DataType.ReviewDataType;
import com.SSUAndroidProject.fairy.R;
import com.SSUAndroidProject.fairy.UserInfo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Write_Review extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public  static boolean comeback=false;

    final int PICK_PHOTO = 1;
    final int CROP_PHOTO = 2;

    private ReviewDataType diaryData;
    private boolean isRewrite;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ReviewDataType Diarydata = new ReviewDataType();
    private InfoDataType Infodata = new InfoDataType();
    private UserInfo userInfo;

    ImageView ImageView_Photo;
    ImageButton Button_Add_Photo;
    Bitmap finalbitmap = null;

    EditText EditText_Title;
    EditText EditText_Text;

    String DataBase_Date = null ;
    String DataBase_title = null;
    String DataBase_text = null;

    private ReviewDataType tempData = new ReviewDataType();;

    private Context mContext;

    private CheckBox checkBox; // 체크박스 아이디 받을 변수
    private boolean checkBoxClosed; // 체크박스의 체크여부를 판단하는 변수
    private RatingBar ratingBar; // 레이팅바 아이디 받을 변수
    private TextView Name;
    private TextView Email;
    private ImageView Festival_Info_IMG;
    private TextView Festival_Info_Title;
    private TextView Festival_Info_date;
    private TextView Festival_Info_location;
    private CircleImageView Profile;
    // 125번 라인 참조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        mContext = this.getApplicationContext();
        EditText_Title = (EditText) findViewById(R.id.Title_Diary);
        EditText_Text = (EditText)findViewById(R.id.Text_Diary);
        Toolbar toolbar = (Toolbar)findViewById(R.id.diary_write_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        diaryData = intent.getParcelableExtra("data");
        isRewrite = intent.getBooleanExtra("isRewrite",false);
        Infodata = intent.getParcelableExtra("FestivalInfo");

        checkBox = (CheckBox)findViewById(R.id.review_state_closed); // 체크박스 아이디 연결
        ratingBar = (RatingBar)findViewById(R.id.Review_write_rating_bar); // 레이팅바 아이디 연결
        Name = (TextView)findViewById(R.id.Write_Review_Name);
        Email = (TextView)findViewById(R.id.Write_Review_Email);
        Festival_Info_IMG = (ImageView)findViewById(R.id.write_festiv_img);
        Festival_Info_Title = (TextView)findViewById(R.id.write_festiv_title);
        Festival_Info_date = (TextView)findViewById(R.id.write_festiv_date);
        Festival_Info_location=(TextView)findViewById(R.id.write_festiv_place);
        Profile=(CircleImageView)findViewById(R.id.Review_detail_Userimg);

        database.getReference()
                .child("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo=dataSnapshot.getValue(UserInfo.class);
                Name.setText(userInfo.getName());
                Email.setText(userInfo.getEmail());
                Glide.with(mContext).load(userInfo.getProfile())
                        .into(Profile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //  Action Bar
        if(!isRewrite) {
            Picasso.with(mContext).load(Infodata.getStrMainImg())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.no_image2)
                    .fit()
                    .into(Festival_Info_IMG);

            SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd, HH:mm:ss a", Locale.KOREA );
            Date currentTime = new Date ();

            DataBase_Date = formatter.format( currentTime );
            Festival_Info_Title.setText(Infodata.getStrTitle());
            Festival_Info_date.setText(Infodata.getStrStartDate());
            Festival_Info_location.setText(Infodata.getStrPlace());
            Diarydata.setStrFestivalDate(Infodata.getStrStartDate());
            Diarydata.setStrFestivalImg(Infodata.getStrMainImg());
            Diarydata.setStrFestivalName(Infodata.getStrTitle());
            Diarydata.setLocation(Infodata.getStrPlace());
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back2);
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitleChange("리뷰작성");
        }
        else{
            tempData.CopyData(diaryData);
            Picasso.with(mContext).load(tempData.getStrFestivalImg())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.no_image2)
                    .fit()
                    .into(Festival_Info_IMG);
            Festival_Info_Title.setText(tempData.getStrFestivalName());
            Festival_Info_date.setText(tempData.getStrFestivalDate());
            Festival_Info_location.setText(tempData.getLocation());
            EditText_Title.setText(tempData.getStrTitle());
            EditText_Text.setText(tempData.getStrMainText());
            ratingBar.setRating(tempData.getStar());
            if(!tempData.isPrivacy())
                checkBox.setChecked(false);
            else
                checkBox.setChecked(true);
            DataBase_Date = tempData.getStrDate() ;
            DataBase_title = tempData.getStrTitle();
            DataBase_text = tempData.getStrMainText();

            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back2);
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitleChange("리뷰수정");
        }
        checkBox.setOnCheckedChangeListener(this); // 체크박스 리스너 // 함수는 375번 라인 참조
        Festival_Info_Title.setSingleLine(true);
        Festival_Info_Title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        Festival_Info_Title.setMarqueeRepeatLimit(-1);
        Festival_Info_Title.setSelected(true);
        Festival_Info_Title.setClickable(false);
    }
    public void Save_Diary(){
        DataBase_title = EditText_Title.getText().toString();
        DataBase_text = EditText_Text.getText().toString();
        if(DataBase_title.trim().getBytes().length<=0){
            Toast.makeText(getApplicationContext(),"제목을 입력하시오.",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(DataBase_text.trim().getBytes().length<=0){
            Toast.makeText(getApplicationContext(),"본문을 입력하시오.",Toast.LENGTH_SHORT).show();
            return;
        }
        Diarydata.setPrivacy(checkBoxClosed);
        Diarydata.setStar(ratingBar.getRating());

        if(!isRewrite) {

            database.getReference()
                    .child("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userInfo=dataSnapshot.getValue(UserInfo.class);
                    Diarydata.setStrDate(DataBase_Date);
                    Diarydata.setStrMainText(DataBase_text);
                    Diarydata.setStrTitle(DataBase_title);
                    Diarydata.setStrUserEmail(userInfo.getEmail());
                    Diarydata.setStrUsername(userInfo.getName());
                    Diarydata.setStrUserProfile(userInfo.getProfile());
                    database.getReference()
                            .child("Reviews")
                            .push()
                            .setValue(Diarydata);
                    // Key 추가
                    database.getReference()
                            .child("Reviews")
                            .addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                ReviewDataType temp =  snapshot.getValue(ReviewDataType.class);
                                                temp.setStrKey(snapshot.getKey());
                                                database.getReference()
                                                        .child("Reviews")
                                                        .child(snapshot.getKey())
                                                        .setValue(temp);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }else{
            SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd, HH:mm:ss a", Locale.KOREA );
            Date currentTime = new Date ();

            DataBase_Date = formatter.format( currentTime );
            tempData.setStrDate(DataBase_Date);
            tempData.setStrMainText(DataBase_text);
            tempData.setStrTitle(DataBase_title);
            tempData.setStar(ratingBar.getRating());
            tempData.setPrivacy(checkBox.isChecked());
            database.getReference()
                    .child("Reviews")
                    .child(tempData.getStrKey())
                    .setValue(tempData);
            Intent returnintent = new Intent();
            returnintent.putExtra("rewriteData",tempData);
            setResult(1,returnintent);
        }
        Toast.makeText(getApplicationContext(), "리뷰를 저장하였습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkBoxClosed = checkBox.isChecked();
    }
    //Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_write, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_save:
                Save_Diary();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void setTitleChange(String title){
        getSupportActionBar().setTitle(title);
    }

    /*public void Save_Diary(){
        if(comeback)
            CachePictureURI = saveCacheBitmap(finalbitmap);
        DataBase_title = EditText_Title.getText().toString();
        DataBase_text = EditText_Text.getText().toString();
        if(DataBase_title.trim().getBytes().length<=0){
            Toast.makeText(getApplicationContext(),"제목을 입력하시오.",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(DataBase_text.trim().getBytes().length<=0){
            Toast.makeText(getApplicationContext(),"본문을 입력하시오.",Toast.LENGTH_SHORT).show();
            return;
        }
        Diarydata.setPrivacy(checkBoxClosed);
        Diarydata.setStar(ratingBar.getRating());
        if(!isRewrite) {
            storageRef = storage.getReferenceFromUrl("gs://ssuandroid-1228.appspot.com");
            final Uri file = Uri.fromFile(new File(CachePictureURI));
            StorageReference riversRef = storageRef
                    .child(mAuth.getCurrentUser().getUid())
                    .child(file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Diarydata.setStrDate(DataBase_Date);
                    Diarydata.setStrMainText(DataBase_text);
                    Diarydata.setStrTitle(DataBase_title);
                    Diarydata.setStrUserEmail(mAuth.getCurrentUser().getUid());
                    Diarydata.setStrImgPath(downloadUrl.toString());
                    Diarydata.setStrUsername(file.getLastPathSegment());
                    database.getReference().child(mAuth.getCurrentUser().getUid()).child("Review").push().setValue(Diarydata);
                }
            });
            File removePicture = new File(CachePictureURI);
            if (removePicture.exists())
                removePicture.delete();
        }else{
            if(comeback){
                removeDiary();
                storageRef = storage.getReferenceFromUrl("gs://ssuandroid-1228.appspot.com");
                final Uri file = Uri.fromFile(new File(CachePictureURI));
                StorageReference riversRef = storageRef
                        .child(mAuth.getCurrentUser().getUid())
                        .child(file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        tempData.setStrImgPath(downloadUrl.toString());
                        tempData.setStrUsername(file.getLastPathSegment());
                        tempData.setStrDate(DataBase_Date);
                        tempData.setStrMainText(DataBase_text);
                        tempData.setStrTitle(DataBase_title);
                        database.getReference()
                                .child(mAuth.getCurrentUser().getUid())
                                .child(tempData.getStrKey())
                                .setValue(tempData);
                    }
                });
                File removePicture = new File(CachePictureURI);
                if (removePicture.exists())
                    removePicture.delete();
            }else {
                tempData.setStrDate(DataBase_Date);
                tempData.setStrMainText(DataBase_text);
                tempData.setStrTitle(DataBase_title);
                database.getReference()
                        .child(mAuth.getCurrentUser().getUid())
                        .child(tempData.getStrKey())
                        .setValue(tempData);
            }
        }
        Toast.makeText(getApplicationContext(),"일기를 저장하였습니다.",Toast.LENGTH_SHORT).show();
        finish();
    }*/
    private void removeDiary(){
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storage.getReference()
                .child(mAuth.getCurrentUser().getUid())
                .child(tempData.getStrUsername())
                .delete();
    }
    public void Add_Photo(View v){
        comeback = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO&& resultCode == Activity.RESULT_OK) {
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
            ImageView_Photo.setImageBitmap(finalbitmap);
            Button_Add_Photo.setAlpha(65);
        }
    }
    @Nullable
    private String saveCacheBitmap(Bitmap bitmap){
        String FileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/FairyCache";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FairyCache");
            if(!file.isDirectory()){
                file.mkdir();
            }
            FileOutputStream outputStream = new FileOutputStream(file+"/"+FileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return path+"/"+FileName;
    }

}
