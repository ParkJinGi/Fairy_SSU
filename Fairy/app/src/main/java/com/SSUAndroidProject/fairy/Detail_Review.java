package com.SSUAndroidProject.fairy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SSUAndroidProject.fairy.Adapter.CommentListViewAdapter;
import com.SSUAndroidProject.fairy.DataType.CommentDataType;
import com.SSUAndroidProject.fairy.DataType.ReviewDataType;
import com.SSUAndroidProject.fairy.Fragment.Add_Review.Write_Review;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detail_Review extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private TextView text;
    private TextView date;
    private TextView Name;
    private TextView Email;
    private ImageView Festival_Info_IMG;
    private TextView Festival_Info_Title;
    private TextView Festival_Info_date;
    private TextView Festival_Info_location;
    private ReviewDataType ReviewData;
    private Context mContext;
    private RatingBar ratingBar;
    private FirebaseAuth Auth;
    private FirebaseDatabase database;
    private CircleImageView profile;
    private CommentListViewAdapter adapter;
    private CircleImageView comment_profile;
    private EditText comment_text;
    private Button comment_submit;
    private ListView comment_list;
    private UserInfo userInfo;
    private TextView privacy;
    private LinearLayout diary;
    private TextView detail_title;
    private TextView detail_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);
        mContext = this.getApplicationContext();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        ReviewData = new ReviewDataType();
        ReviewData =intent.getParcelableExtra("DiaryData");
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        toolbar = (Toolbar)findViewById(R.id.Review_detail_toolbar);
        Festival_Info_IMG = (ImageView)findViewById(R.id.Review_detail_img);
        title = (TextView)findViewById(R.id.Review_detail_title);
        text = (TextView)findViewById(R.id.Review_detail_text);
        date=(TextView)findViewById(R.id.Review_detail_Date);
        Festival_Info_Title = (TextView)findViewById(R.id.Review_detail_titleFes);
        Festival_Info_date = (TextView)findViewById(R.id.Review_detail_dateFes);
        Festival_Info_location=(TextView)findViewById(R.id.Review_detail_placeFes);
        Name=(TextView)findViewById(R.id.Review_detail_Name);
        Email =(TextView)findViewById(R.id.Review_detail_Email);
        ratingBar=(RatingBar)findViewById(R.id.Review_detail_rating_bar);
        profile=(CircleImageView)findViewById(R.id.Review_detail_Userimg) ;
        privacy=(TextView)findViewById(R.id.review_datail_privacy);
        comment_profile = (CircleImageView) findViewById(R.id.comment_profile);
        comment_submit = (Button) findViewById(R.id.comment_submit);
        comment_text = (EditText) findViewById(R.id.comment_text);
        comment_list = (ListView) findViewById(R.id.sns_detail_comment);
        diary = (LinearLayout)findViewById(R.id.diary_layout);
        adapter = new CommentListViewAdapter();
        database.getReference()
                .child("Comment")
                .child(ReviewData.getStrKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        adapter.reset();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            CommentDataType temp = snapshot.getValue(CommentDataType.class);
                            adapter.addItem(temp);
                        }
                        adapter.notifyDataSetChanged();
                        comment_list.setSelection(adapter.getCount()-1);
                        setListViewHeightBasedOnChildren(comment_list);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        comment_list.setAdapter(adapter);
        setSupportActionBar(toolbar);
        //  Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back2);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitleChange("리뷰");
        setData();

        detail_title = (TextView)findViewById(R.id.Review_detail_titleFes);
        detail_title.setSingleLine(true);
        detail_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        detail_title.setMarqueeRepeatLimit(-1);
        detail_title.setSelected(true);
        detail_title.setClickable(false);

        detail_date = (TextView)findViewById(R.id.Review_detail_dateFes);
        detail_date.setSingleLine(true);
        detail_date.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        detail_date.setMarqueeRepeatLimit(-1);
        detail_date.setSelected(true);
        detail_date.setClickable(false);

        comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_text.getText().toString().trim().length()>0) {
                    database.getReference()
                            .child("Users")
                            .child(Auth.getCurrentUser().getUid())
                            .child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                            CommentDataType temp = new CommentDataType();
                            temp.setUserprofile(userInfo.getProfile());
                            temp.setUsername(userInfo.getName());
                            temp.setComment(comment_text.getText().toString());
                            SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd, HH:mm:ss a", Locale.KOREA );
                            Date currentTime = new Date();
                            temp.setDate(formatter.format( currentTime ));
                            database.getReference()
                                    .child("Comment")
                                    .child(ReviewData.getStrKey())
                                    .push()
                                    .setValue(temp);
                            comment_text.setText("");
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }
    //Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_detail, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_rewrite:
                Intent intent = new Intent(this, Write_Review.class);
                intent.putExtra("data", ReviewData);
                intent.putExtra("isRewrite",true);
                startActivityForResult(intent,1);
                return true;

            case R.id.action_remove:
                AlertDialog dialog =
                        new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                                .setMessage("정말 리뷰를 삭제하시겠습니까?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeDiary();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                return true;

            case R.id.action_save_diary:
                AlertDialog dialog2 =
                        new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                                .setMessage("일기를 저장하시겠습니까?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        detail_title.setSelected(false);
                                        detail_date.setSelected(false);
                                        privacy.setVisibility(View.INVISIBLE);
                                        save_diary();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void save_diary(){
        diary.setDrawingCacheEnabled(true);
        diary.setDrawingCacheBackgroundColor(Color.WHITE);
        diary.buildDrawingCache();
        Bitmap bitmap =diary.getDrawingCache();
        String FileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Fairy");
            if(!file.isDirectory()){
                file.mkdir();
            }
            FileOutputStream outputStream = new FileOutputStream(file+"/"+FileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
            bitmap.recycle();
            Toast.makeText(mContext, "일기로 저장하였습니다.", Toast.LENGTH_SHORT).show();
            privacy.setVisibility(View.VISIBLE);
            detail_date.setSelected(true);
            detail_title.setSelected(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void removeDiary(){
        database.getReference()
                .child("Comment")
                .child(ReviewData.getStrKey())
                .setValue(null);
        database.getReference()
                .child("Reviews")
                .child(ReviewData.getStrKey())
                .setValue(null);
        Toast.makeText(mContext,"리뷰를 삭제하였습니다.",Toast.LENGTH_SHORT).show();
        finish();
    }
    protected void setTitleChange(String title){
        getSupportActionBar().setTitle(title);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1) {
            ReviewData = data.getParcelableExtra("rewriteData");
            setData();
        }
    }
    private void setData(){
        Picasso.with(mContext).load(ReviewData.getStrFestivalImg())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image2)
                .fit()
                .into(Festival_Info_IMG);
        title.setText(ReviewData.getStrTitle());
        text.setText(ReviewData.getStrMainText());
        date.setText(ReviewData.getStrDate());
        Festival_Info_Title.setText(ReviewData.getStrFestivalName());
        Festival_Info_date.setText(ReviewData.getStrFestivalDate());
        Festival_Info_location.setText(ReviewData.getLocation());
        Name.setText(ReviewData.getStrUsername());
        Email.setText(ReviewData.getStrUserEmail());
        ratingBar.setRating(ReviewData.getStar());
        Glide.with(mContext).load(ReviewData.getStrUserProfile())
                .into(profile);
        if(!ReviewData.isPrivacy())
            privacy.setText("공개");
        else
            privacy.setText("비공개");
        database.getReference()
                .child("Users")
                .child(Auth.getCurrentUser().getUid())
                .child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                Glide.with(mContext).load(userInfo.getProfile())
                        .into(comment_profile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
