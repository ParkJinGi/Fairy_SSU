package com.SSUAndroidProject.fairy.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.SSUAndroidProject.fairy.DataType.ReviewDataType;
import com.SSUAndroidProject.fairy.Detail_Review;
import com.SSUAndroidProject.fairy.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ReviewFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ReviewFragmentRecyclerViewAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<ReviewDataType> aListReview;
    public ReviewFragmentRecyclerViewAdapter(Context context, ArrayList<ReviewDataType> aListDiary) {
        this.mContext = context;
        this.aListReview = aListDiary;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_review,null);
        return new ReviewFragmentRecyclerViewAdapter.ViewHolder(v);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;
        holder.tvDate.setText(aListReview.get(pos).getStrDate());
        holder.tvDate.setText(aListReview.get(pos).getStrDate());
        holder.tvTitle.setText(aListReview.get(pos).getStrTitle());
        Picasso.with(mContext).load(aListReview.get(pos).getStrFestivalImg())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image2)
                .fit()
                .into(holder.ivImg);
        holder.tvName.setText(aListReview.get(pos).getStrUsername());
        holder.tvEmail.setText(aListReview.get(pos).getStrUserEmail());
        holder.tvTitleFes.setText(aListReview.get(pos).getStrFestivalName());
        holder.tvDateFes.setText(aListReview.get(pos).getStrFestivalDate());
        holder.tvPlace.setText(aListReview.get(pos).getLocation());
        Glide.with(mContext).load(aListReview.get(pos).getStrUserProfile())
                .into(holder.profile);
        if(!aListReview.get(pos).isPrivacy())
            holder.tvprivacy.setText("공개");
        else
            holder.tvprivacy.setText("비공개");
        //  카드뷰 클릭 이벤트
        holder.ratingBar.setRating(aListReview.get(pos).getStar());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDataType diaryData = new ReviewDataType();
                diaryData.CopyData(aListReview.get(pos));
                Intent intent = new Intent(mContext.getApplicationContext(), Detail_Review.class);
                intent.putExtra("DiaryData", diaryData);
                mContext.startActivity(intent);
            }
        });
        holder.tvTitle.setSingleLine(true);
        holder.tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvTitle.setMarqueeRepeatLimit(-1);
        holder.tvTitle.setSelected(true);
        holder.tvTitle.setClickable(false);

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardview;
        ImageView ivImg;
        TextView tvTitle;
        TextView tvDate;
        TextView tvTitleFes;
        TextView tvDateFes;
        TextView tvName;
        TextView tvEmail;
        TextView tvprivacy;
        TextView tvPlace;
        RatingBar ratingBar;
        ImageView profile;
        public ViewHolder(View itemView) {
            super(itemView);
            cardview = (CardView)itemView.findViewById(R.id.review_cardview);
            tvPlace=(TextView)itemView.findViewById(R.id.review_card_place) ;
            ivImg = (ImageView)itemView.findViewById(R.id.review_card_imageview);
            tvTitle = (TextView)itemView.findViewById(R.id.review_card_title);
            tvDate = (TextView)itemView.findViewById(R.id.review_card_date);
            tvTitleFes= (TextView)itemView.findViewById(R.id.review_card_Festitle);
            tvDateFes= (TextView)itemView.findViewById(R.id.review_card_dateFes);
            tvName= (TextView)itemView.findViewById(R.id.review_card_Name);
            tvEmail= (TextView)itemView.findViewById(R.id.review_card_Email);
            tvprivacy=(TextView)itemView.findViewById(R.id.review_card_privacy);
            ratingBar=(RatingBar)itemView.findViewById(R.id.review_card_rating_bar);
            profile=(ImageView)itemView.findViewById(R.id.Review_detail_Userimg);
        }
    }
    @Override
    public int getItemCount() {
        return aListReview.size();
    }
}
