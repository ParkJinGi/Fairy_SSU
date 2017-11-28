package com.SSUAndroidProject.fairy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.SSUAndroidProject.fairy.DataType.CommentDataType;
import com.SSUAndroidProject.fairy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 박PC on 2017-11-18.
 */

public class CommentListViewAdapter extends BaseAdapter{

    private ArrayList<CommentDataType> aListComment = new ArrayList<>();
    private Context mContext;


    @Override
    public int getCount() {
        return aListComment.size();
    }

    @Override
    public Object getItem(int position) {
        return aListComment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        mContext = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_list_item, parent, false);
        }

        CircleImageView profile=(CircleImageView)convertView.findViewById(R.id.comment_list_profile);
        TextView name=(TextView)convertView.findViewById(R.id.comment_list_name);
        TextView comment=(TextView)convertView.findViewById(R.id.comment_list_text);
        TextView date = (TextView)convertView.findViewById(R.id.comment_list_date) ;

        CommentDataType commentDataType = aListComment.get(pos);
        Picasso.with(mContext).load(aListComment.get(pos).getUserprofile())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image2)
                .fit()
                .into(profile);
        name.setText(aListComment.get(pos).getUsername());
        comment.setText(aListComment.get(pos).getComment());
        date.setText(aListComment.get(pos).getDate());
        return convertView;
    }

    public void addItem(CommentDataType dataType) {
        CommentDataType item = dataType;
        aListComment.add(item);
    }

    public void reset(){
        aListComment.clear();
    }
}
