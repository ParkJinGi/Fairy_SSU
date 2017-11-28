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

import com.SSUAndroidProject.fairy.Adapter.ReviewFragmentRecyclerViewAdapter;
import com.SSUAndroidProject.fairy.DataType.ReviewDataType;
import com.SSUAndroidProject.fairy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;



public class ReviewFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";   //  Position값 받아올 구분자
    private static final int POSITION_DIARY = 1;
    private int mPage;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ReviewFragmentRecyclerViewAdapter mAdapter;
    private ArrayList<ReviewDataType> aListReview = new ArrayList<>();
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    //  Constructor
    public ReviewFragment(){
    }

    //  Constructor
    @SuppressLint("ValidFragment")
    public ReviewFragment(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        this.setArguments(args);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if(mPage == POSITION_DIARY){
            view = inflater.inflate(R.layout.fragment_review, container, false);
        }
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.diary_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        database.getReference().child("Reviews")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                aListReview.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    ReviewDataType temp =  snapshot.getValue(ReviewDataType.class);
                                    if(temp.getStrUserEmail().equals(mAuth.getCurrentUser().getEmail().toString()))
                                        aListReview.add(temp);

                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
        mAdapter = new ReviewFragmentRecyclerViewAdapter(getActivity(), aListReview);
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
}
