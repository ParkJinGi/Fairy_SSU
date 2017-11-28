package com.SSUAndroidProject.fairy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.PoPupImage;
import com.SSUAndroidProject.fairy.R;
import com.hanks.htextview.rainbow.RainbowTextView;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import static com.SSUAndroidProject.fairy.R.attr.title;


public class InfoFragmentRecyclerViewAdapter extends ArrayAdapter<InfoDataType> {

    Context mContext;
    ArrayList<InfoDataType> aListInfo;

    private int mYear,mMonth,mDay;
    private String today,endday,dday;

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;

    public InfoFragmentRecyclerViewAdapter(Context context, ArrayList<InfoDataType> aListInfo) {
        super(context,0,aListInfo);
        this.mContext = context;
        this.aListInfo=aListInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final InfoDataType item = getItem(position);
        //d-day
        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH)+1;
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        String tempmonth;
        String tempday;
        if(((mMonth)/10) <1)
            tempmonth = "0"+String.valueOf(mMonth);
        else
            tempmonth =String.valueOf(mMonth);
        if((mDay/10)<1)
            tempday = "0"+String.valueOf(mDay);
        else
            tempday =String.valueOf(mDay);
        endday = item.getStrEndDate();
        String temp[] = endday.split("-");
        endday = temp[0]+temp[1]+temp[2];
        today = String.valueOf(mYear)+tempmonth+tempday;

        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder holder;
        if (cell == null) {
            holder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.card_info, parent, false);
            holder.ivImg = (ImageView)cell.findViewById(R.id.info_cardview_imageview);
            holder.tvTitle = (TextView)cell.findViewById(R.id.info_cardview_title);
            holder.tvDate = (TextView)cell.findViewById(R.id.info_cardview_date);
            holder.tvPlace = (TextView)cell.findViewById(R.id.info_cardview_place);
            holder.tvFee = (TextView)cell.findViewById(R.id.info_cardview_fee);
            holder.tvDDay = (RainbowTextView)cell.findViewById(R.id.info_cardview_due);
            holder. foldingCell = (FoldingCell)cell.findViewById(R.id.folding_cell);

            holder.mainIv = (ImageView)cell.findViewById(R.id.main_iv);
            holder.titleTv = (TextView)cell.findViewById(R.id.info_detail_title);
            holder.dateTv = (TextView)cell.findViewById(R.id.info_detail_date);
            holder.timeTv = (TextView)cell.findViewById(R.id.info_detail_time);
            holder.placeTv = (TextView)cell.findViewById(R.id.info_detail_place);
            holder.targetTv = (TextView)cell.findViewById(R.id.info_detail_target);
            holder.feeTv = (TextView)cell.findViewById(R.id.info_detail_fee);

            holder.inquiryTv = (TextView)cell.findViewById(R.id.info_detail_inquiry);
            holder.share=(SubmitButton) cell.findViewById(R.id.bt_share);
            holder.link=(SubmitButton)cell.findViewById(R.id.bt_Link);
            holder.call=(SubmitButton)cell.findViewById(R.id.bt_call);
            cell.setTag(holder);
        }else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            holder = (ViewHolder) cell.getTag();
        }

        try {
            dday = Long.toString(diffOfDate(today, endday));
            if(dday.equals("0"))
                holder.tvDDay.setText("D-Day");
            else {
                holder.tvDDay.setText("D-"+dday);
            }
        }catch (Exception e){}


        //  이미지 스트링이 없으면 비운다.
        if(item.getStrMainImg().isEmpty())
        {
            holder.ivImg.setVisibility(View.GONE);
        }
        else
        {
            //  Picasso 라이브러리를 통해 URL에 해당하는 이미지를 가져와 뷰에 넣는다.
            Picasso.with(mContext).load(item.getStrMainImg())
                    .placeholder(R.drawable.loading_image)                              // 이미지 불러오는 동안 이미지
                    .error(R.drawable.no_image2)                                         // 다운로드 실패 시, 이미지
                    .fit()                                                               // 이미지뷰에 맞추기
                    .into(holder.ivImg);
        }

        //  카드뷰의 각 값 세팅
        holder.tvTitle.setText(item.getStrTitle());
        holder.tvDate.setText(item.getStrStartDate()+"~"+item.getStrEndDate());
        holder.tvPlace.setText(item.getStrPlace());

        //  무/유료 구분
        String strIsFree = item.getStrIsFree();
        if(strIsFree.equals("1"))
        {
            holder.tvFee.setText(mContext.getString(R.string.text_free));
        }
        else if(strIsFree.equals("0"))
        {
            holder.tvFee.setText(mContext.getString(R.string.text_not_free));
        }
        else {
            holder.tvFee.setText(strIsFree);
        }
        Picasso.with(mContext).load(item.getStrMainImg())
                .placeholder(R.drawable.loading_image)                              // 이미지 불러오는 동안 이미지
                .error(R.drawable.no_image2)                                  // 다운로드 실패 시, 이미지
                .fit()                                                            // 이미지뷰에 맞추기
                .into(holder.mainIv);
        holder.titleTv.setText(item.getStrTitle());
        holder.dateTv.setText(item.getStrStartDate() + " ~ " + item.getStrEndDate());
        holder.timeTv.setText(item.getStrTime());
        holder.placeTv.setText(item.getStrPlace());
        holder.targetTv.setText(item.getStrUseTarget());
        if("1".equals(item.getStrIsFree())) //무료일 경우
        {
            holder.feeTv.setText("무료");
        }
        else // 유료일 경우
        {
            holder.feeTv.setText(item.getStrUseFee());
        }

        holder.inquiryTv.setText(item.getStrInquiry());

        holder.mainIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PoPupImage.class);
                intent.putExtra("Uri",item.getStrMainImg());
                mContext.startActivity(intent);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,title);
                intent.putExtra(Intent.EXTRA_TEXT, item.getStrOrgLink());
                Intent chooser = Intent.createChooser(intent, "공유");
                mContext.startActivity(chooser);
            }
        });
        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUrl = item.getStrOrgLink();
                if(strUrl == null || strUrl.length() <= 0)
                {
                    Toast.makeText(mContext, "연결할 수 있는 인터넷 주소가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
                mContext.startActivity(intent);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInquiry = item.getStrInquiry();
                int iHyphen = 0;
                String strNum = "";

                if(strInquiry == null || strInquiry.length() <= 0)
                {
                    Toast.makeText(mContext, "연결할 수 있는 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                strInquiry = strInquiry.replace(')', '-');
                for(int i = 0; i < strInquiry.length(); i++) {
                    if(strInquiry.charAt(i) == '-')
                    {
                        iHyphen++;
                    }
                    if(strInquiry.charAt(i) == '~')
                    {
                        break;
                    }
                    if(strInquiry.charAt(i) >= '0' && strInquiry.charAt(i) <= '9')
                    {
                        strNum += strInquiry.charAt(i);
                    }
                }
                String strDialNumber = "tel:";
                if(iHyphen == 1)
                {
                    if(strInquiry.contains("1544"))
                    {
                        strDialNumber += strNum;
                    }
                    else
                    {
                        strDialNumber += "02";
                        strDialNumber += strNum;
                    }
                }
                else if(iHyphen == 2)
                {
                    strDialNumber += strNum;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(strDialNumber));
                mContext.startActivity(intent);
            }
        });
        holder.call.reset();
        holder.share.reset();
        holder.link.reset();
        return cell;
    }


    public class ViewHolder  {
        ImageView ivImg;
        TextView tvTitle;
        TextView tvDate;
        TextView tvPlace;
        TextView tvFee;
        RainbowTextView tvDDay;
        FoldingCell foldingCell;
        TextView titleTv;
        TextView dateTv ;
        TextView timeTv;
        TextView placeTv ;
        TextView targetTv;
        TextView feeTv;
        ImageView mainIv;

        TextView inquiryTv;
        SubmitButton share;
        SubmitButton call;
        SubmitButton link;
    }
    public static long diffOfDate(String begin, String end) throws Exception
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);
        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays;
    }


    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

}
