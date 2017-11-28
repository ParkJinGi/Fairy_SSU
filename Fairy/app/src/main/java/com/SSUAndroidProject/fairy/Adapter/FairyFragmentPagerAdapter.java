package com.SSUAndroidProject.fairy.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.SSUAndroidProject.fairy.DataType.FilterDataType;
import com.SSUAndroidProject.fairy.Fragment.InfoFragment;
import com.SSUAndroidProject.fairy.Fragment.ReviewFragment;
import com.SSUAndroidProject.fairy.Fragment.SNSFragment;


public class FairyFragmentPagerAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 3;   //페이지 개수
    Context mContext;
    FilterDataType filterData;

    public FairyFragmentPagerAdapter(FragmentManager fm, Context context, FilterDataType filterData) {
        super(fm);
        this.mContext = context;
        this.filterData = filterData;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {

       if(position == 0){
            return new InfoFragment(position,  filterData);
        }
        else if(position == 1){
            return new ReviewFragment(position);
        }
        else{
            return new SNSFragment(position);
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

}
