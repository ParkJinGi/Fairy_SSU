package com.SSUAndroidProject.fairy.Fragment.Add_Review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.SSUAndroidProject.fairy.Adapter.FestivalListViewAdapter;
import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.R;

import java.util.ArrayList;

public class getFestivalInfo extends AppCompatActivity {

    private ListView FestivalList;
    private FestivalListViewAdapter adapter;
    private ArrayList<InfoDataType> infolist = new ArrayList<>();
    private EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_festivallist);
        Intent intent = getIntent();
        infolist = intent.getParcelableArrayListExtra("test");
        adapter = new FestivalListViewAdapter();
        FestivalList = (ListView) findViewById(R.id.FestivalList);
        FestivalList.setAdapter(adapter);
        search = (EditText) findViewById(R.id.festival_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                if (filterText.length() >= 0) {
                    ((FestivalListViewAdapter)FestivalList.getAdapter()).getFilter().filter(filterText) ;
                } else {
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        for (int i = 0; i < infolist.size(); i++) {
            adapter.addItem(infolist.get(i).getStrMainImg(), infolist.get(i).getStrTitle(), infolist.get(i).getStrStartDate() + " ~ " + infolist.get(i).getStrEndDate(),infolist.get(i).getStrPlace());
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    public void onClick_search_cancel2(View v){
        search.setText("");
    }
}
