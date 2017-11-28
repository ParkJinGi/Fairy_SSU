package com.SSUAndroidProject.fairy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.Fragment.Add_Review.Write_Review;
import com.SSUAndroidProject.fairy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 박PC on 2017-11-09.
 */

public class FestivalListViewAdapter extends BaseAdapter implements Filterable{

    private LinearLayout touch;
    private ArrayList<InfoDataType> aListInfo = new ArrayList<>();
    private ArrayList<InfoDataType> filteredItemList = aListInfo;
    Context context;
    Filter listFilter;

    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listviewitem, parent, false);
        }
        touch = (LinearLayout)convertView.findViewById(R.id.FestivalCard);
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.listimg) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.listtitle) ;
        TextView date = (TextView) convertView.findViewById(R.id.listdate) ;

        InfoDataType listViewItem = filteredItemList.get(pos);
        Picasso.with(context).load(filteredItemList.get(pos).getStrMainImg())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image2)
                .fit()
                .into(iconImageView);
        titleTextView.setText(listViewItem.getStrTitle());
        date.setText(listViewItem.getStrStartDate());


        touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Write_Review.class);
                InfoDataType data = new InfoDataType();
                data.CopyData(filteredItemList.get(pos));
                intent.putExtra("FestivalInfo",data);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position) ;
    }


    public void addItem(String Imgpath, String title, String date,String Location) {
        InfoDataType item = new InfoDataType();
        item.setStrMainImg(Imgpath);
        item.setStrTitle(title);
        item.setStrStartDate(date);
        item.setStrPlace(Location);
        aListInfo.add(item);
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }
        return listFilter ;
    }
    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;
            if (constraint == null || constraint.length() == 0) {
                results.values = aListInfo ;
                results.count = aListInfo.size() ;
            } else {
                ArrayList<InfoDataType> itemList = new ArrayList<InfoDataType>() ;
                for (InfoDataType item : aListInfo) {
                    if (item.getStrTitle().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }
                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // update listview by filtered data list.
            filteredItemList = (ArrayList<InfoDataType>) results.values ;
            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}
