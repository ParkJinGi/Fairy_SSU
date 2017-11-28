package com.SSUAndroidProject.fairy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SSUAndroidProject.fairy.Adapter.SNSFragmentRecyclerViewAdapter;
import com.SSUAndroidProject.fairy.DataType.ReviewDataType;
import com.SSUAndroidProject.fairy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SNSFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";   //  Position값 받아올 구분자
    public static final int POSITION_SETTING = 2;
    private int mPage;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SNSFragmentRecyclerViewAdapter mAdapter;
    private FirebaseDatabase database;
    private ArrayList<ReviewDataType> aListReview = new ArrayList<>();
    private ArrayList<ReviewDataType> tempList = new ArrayList<>();

    //  Constructor
    public SNSFragment() {

    }

    //  Constructor
    @SuppressLint("ValidFragment")
    public SNSFragment(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        this.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        database = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (mPage == POSITION_SETTING) {
            view = inflater.inflate(R.layout.fragment_sns, container, false);
        }
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.SNS_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        database.getReference().child("Reviews")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                aListReview.clear();
                                tempList.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ReviewDataType temp = snapshot.getValue(ReviewDataType.class);
                                    if (!temp.isPrivacy())
                                        tempList.add(temp);
                                }
                                if (tempList.size() > 0)
                                    aListReview.addAll(SortBydate(tempList));
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
        mAdapter = new SNSFragmentRecyclerViewAdapter(getActivity(), aListReview);
        mRecyclerView.setAdapter(mAdapter);
        //  Floating Button 연결.
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.diary_fab);
        fab.attachToRecyclerView(mRecyclerView);
        //  Floating Button 클릭 리스너
        //  클릭시 맨 위로
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });
        return view;
    }

    public ArrayList<ReviewDataType> SortBydate(ArrayList<ReviewDataType> tempList) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss a", Locale.KOREA);
        ArrayList<ReviewDataType> SortedList = new ArrayList<ReviewDataType>();
        Date date = null;
        Date tempdate = null;

        SortedList.add(tempList.remove(0));
        try {
            for (ReviewDataType data : tempList) {
                date = formatter.parse(data.getStrDate());
                for (int i = 0; i < SortedList.size(); i++) {
                    tempdate = formatter.parse(SortedList.get(i).getStrDate());
                    if (date.after(tempdate)) {
                        SortedList.add(i, data);
                        break;
                    }
                    if (i == SortedList.size() - 1)
                        SortedList.add(data);
                }
            }
        } catch (Exception e) {
        }
        return SortedList;
    }
}
