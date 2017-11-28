package com.SSUAndroidProject.fairy;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.SSUAndroidProject.fairy.Adapter.FairyFragmentPagerAdapter;
import com.SSUAndroidProject.fairy.DataType.FilterDataType;
import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.Fragment.Add_Review.getFestivalInfo;
import com.SSUAndroidProject.fairy.MoreFunction.Filter;
import com.SSUAndroidProject.fairy.Navigation.Navi_ContactUs;
import com.SSUAndroidProject.fairy.Navigation.Navi_Developers;
import com.SSUAndroidProject.fairy.Navigation.Navi_LicenseInfo;
import com.SSUAndroidProject.fairy.Navigation.Navi_Security.AppLockManager;
import com.SSUAndroidProject.fairy.Navigation.Navi_Security.Navi_Secuity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    public static boolean comeback = false;

    final int PAGE_COUNT = 3;   //페이지 개수
    private DrawerLayout mDrawerLayout;
    private ViewPager viewpager;    // ViewPager에 Fragment 올려서 액티비티 구성.
    private FairyFragmentPagerAdapter mAdapter;
    private Context mContext;

    private TextView UserID;
    private TextView UserName;
    private ImageView UserProfile;
    private UserInfo userInfo;

    private Button Logout;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    Menu mMenu;
    Toolbar toolbar;
    private boolean isoverlay = false;

    //  Filter
    private FilterDataType filterData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        /**********************************************************************************************************/
        Application thisApp = (Application)getApplication(); // 어플 잠금 여부 확인 및 비밀번호 입력  by JinGi
        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(thisApp);
        /**********************************************************************************************************/
        this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        setContentView(R.layout.activity_main);
        //Helper 페이지 보여주는 함수 (Overlay)


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "앱 실행을 위해서는 메모리 읽기 권한을 설정해야 합니다.", Toast.LENGTH_LONG).show();
            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        Intent intent = getIntent();
        isoverlay = intent.getBooleanExtra("first",false);
        if(isoverlay)
            showOverlay();
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mContext = this.getApplicationContext();
        filterData = new FilterDataType();
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        //  Filter 데이터
        Intent intentFilter = getIntent();
        FilterDataType filterDataInput = intentFilter.getParcelableExtra("Filter");
        if(filterDataInput != null) {
            filterData.CopyData(filterDataInput);
        }
        //  Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //  Navigation View 추가.
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        final NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigation_view);
        View view = navigationView.getHeaderView(0);

        UserID = (TextView)view.findViewById(R.id.USER_ID);
        UserName = (TextView)view.findViewById(R.id.USER_Name);
        UserProfile =(ImageView)view.findViewById(R.id.userprofile);

        database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo =  dataSnapshot.getValue(UserInfo.class);
                UserName.setText(userInfo.getName());
                UserID.setText(userInfo.getEmail());
                Glide.with(mContext).load(userInfo.getProfile())
                        .into(UserProfile);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Logout = (Button)view.findViewById(R.id.Logout);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Intent intent;

                int id = menuItem.getItemId();
                switch(id){
                    case R.id.navi_btn_Secuity:
                        startActivity(new Intent(MainActivity.this, Navi_Secuity.class));
                        break;
                    case R.id.navi_btn_ContactUs:
                        intent = new Intent(getApplicationContext(), Navi_ContactUs.class);
                        startActivity(intent);
                        break;
                    case R.id.navi_btn_Developers:
                        intent = new Intent(getApplicationContext(), Navi_Developers.class);
                        startActivity(intent);
                        break;
                    case R.id.navi_btn_LicenseInfo:
                        intent = new Intent(getApplicationContext(), Navi_LicenseInfo.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

        //페이지 2개 미리 띄움. 페이지 이동 시 데이터 로드 때문.
        viewpager = (ViewPager)findViewById(R.id.main_viewpager);
        viewpager.setOffscreenPageLimit(PAGE_COUNT);
        mAdapter = new FairyFragmentPagerAdapter(getSupportFragmentManager(),  mContext, filterData);
        viewpager.setAdapter(mAdapter);

        //  Page 바꿀 때 이벤트 처리.
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position == 0){
                    toolbar.getMenu().clear();
                    setTitleChange("행사정보");
                    getMenuInflater().inflate(R.menu.menu, mMenu);
                }
                else if(position == 1){
                    toolbar.getMenu().clear();
                    setTitleChange("행사리뷰");
                    getMenuInflater().inflate(R.menu.menu_diary, mMenu);
                }
                else if(position == 2)
                {
                    toolbar.getMenu().clear();
                    setTitleChange("SNS");
                    getMenuInflater().inflate(R.menu.menu_sns, mMenu);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Tab 이미지 및 viewpager 등록.
        TabLayout tabLayout = (TabLayout)findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.getTabAt(0).setCustomView(R.layout.icon_info);
        tabLayout.getTabAt(1).setCustomView(R.layout.icon_review);
        tabLayout.getTabAt(2).setCustomView(R.layout.icon_sns);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "메모리 읽기 권한을 사용자가 승인함.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "메모리 읽기 권한 거부됨.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private void showOverlay() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.activity_transparent);
        RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.transparent);
        layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        setTitleChange("행사정보");
        return true;
    }

    private ArrayList<InfoDataType> aListInfo;
    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        aListInfo = mPushEvent.getList();
    }
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch(id)
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_filter:
                comeback = true;
                intent = new Intent(this, Filter.class);
                startActivity(intent);
                return true;

            case R.id.action_write:
                intent = new Intent(this,getFestivalInfo.class);
                intent.putExtra("test",aListInfo);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setTitleChange(String title){
        getSupportActionBar().setTitle(title);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
