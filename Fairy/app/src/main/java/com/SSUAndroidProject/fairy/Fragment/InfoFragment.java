package com.SSUAndroidProject.fairy.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.SSUAndroidProject.fairy.Adapter.InfoFragmentRecyclerViewAdapter;
import com.SSUAndroidProject.fairy.BusProvider;
import com.SSUAndroidProject.fairy.DataType.FilterDataType;
import com.SSUAndroidProject.fairy.DataType.InfoDataType;
import com.SSUAndroidProject.fairy.PushEvent;
import com.SSUAndroidProject.fairy.R;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InfoFragment extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";   //  Position값 받아올 구분자
    public static final int POSITION_INFO = 0;          //  Info Fragment Index
    private int mPage;                                      //  Page Index
    private ListView mRecyclerView;
    InfoFragmentRecyclerViewAdapter mAdapter;

    JSONArray aJson = null;
    ArrayList<InfoDataType> aListInfo;

    String mStrDefaultURL = "http://openapi.seoul.go.kr:8088/727046784e6568663130354363776d6c/json/SearchConcertDetailService/1/";

    ArrayList<String> aListFilter;      //  필터링 될 행사의 Subject Code.


    //  HTML 특수문자 치환할 것은 이곳에 Old->New로 배열에 넣으면 됨.
    String[] arrStrOld = {"&#39;"};
    String[] arrStrNew = {"`"};

    FilterDataType mFilterData; //  Filter 적용위한 데이터 타입.

    //  Constructor
    public InfoFragment(){

    }

    //  Constructor
    @SuppressLint("ValidFragment")
    public InfoFragment(int page, FilterDataType filterData) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        this.setArguments(args);
        this.mFilterData = filterData;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        if(mPage == POSITION_INFO){
            view = inflater.inflate(R.layout.fragment_info, container, false);
        }

        aListInfo = new ArrayList<InfoDataType>();
        mRecyclerView = (ListView) view.findViewById(R.id.info_recyclerview);


        //  필터링 될 행사 종류를 추가하는 함수.
        if(mFilterData != null)
             AddFilterList();

        /*
        *       /1/1 페이지로부터 전체 인덱스 개수 구한 뒤, 전체 데이터에 대해 요청한다.
        *       위 작업을 모두 수행하는 GetInfoDataFromURL 함수.
        * */
        GetInfoDataFromURL();

        return view;
    }

    /*
    *
    *   콘서트 		        : "1.0"
    *   뮤지컬/오페라	    : "3.0"
    *   연극		        : "5.0"
    *   전시/미술	        : "7.0"
    *   축제		        : "12.0"
    *
    * */
    public void AddFilterList()
    {
        aListFilter = new ArrayList<>();
        if(mFilterData.isbCheckArt() == true)
        {
            aListFilter.add(getString(R.string.subjcode_art));
        }

        if(mFilterData.isbCheckConcert() == true)
        {
            aListFilter.add(getString(R.string.subjcode_concert));
        }

        if(mFilterData.isbCheckDrama() == true)
        {
            aListFilter.add(getString(R.string.subjcode_drama));
        }

        if(mFilterData.isbCheckFestival() == true)
        {
            aListFilter.add(getString(R.string.subjcode_festival));
        }

        if(mFilterData.isbCheckOpera() == true)
        {
            aListFilter.add(getString(R.string.subjcode_opera));
        }
    }

    /*
    *       첫번째 페이지만 받아와서 전체 Count를 알아낸 뒤,
    *       다시 전체에 대한 Request를 보내서 데이터를 가져온다.
    * */
    public void GetInfoDataFromURL()
    {
        String strFirstURL = mStrDefaultURL + "1";
        GetTotalRequest objGetTotalCount = new GetTotalRequest();
        objGetTotalCount.execute(strFirstURL);
    }


    /*
    *
    *       전체 데이터를 받아와서 리스트에 삽입한다.
    * */
    public class GetDataJSON extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressdialog = null;

        //  ProgressDialog Setting
        public GetDataJSON()
        {
            progressdialog = new ProgressDialog(getActivity());
            progressdialog.setTitle("");
            progressdialog.setMessage("잠시만 기다려주세요.");
            progressdialog.setCancelable(false);
        }

        //  ProgressDialog Show
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressdialog != null)
            {
                progressdialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //JSON 데이터 시트 받아온다.
            String uri = params[0];
            BufferedReader br = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String strJsonData;
                while((strJsonData = br.readLine()) != null) {
                    sb.append(strJsonData + "\n");
                }
                return sb.toString().trim();
            }catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String strJson)
        {
            MakeInfoArrayList(strJson);
            if(progressdialog != null)
            {
                progressdialog.dismiss();
            }
        }
    }


    /*
    *       첫번째 페이지만 다운로드하는 부분.
    * */
    public class GetTotalRequest extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressdialog = null;

        public GetTotalRequest()
        {
            progressdialog = new ProgressDialog(getActivity());
            progressdialog.setTitle("");
            progressdialog.setMessage("잠시만 기다려주세요.");
            progressdialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressdialog != null)
            {
                progressdialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //JSON 데이터 시트 받아온다.
            String uri = params[0];
            BufferedReader br = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String strJsonData;
                while((strJsonData = br.readLine()) != null) {
                    sb.append(strJsonData + "\n");
                }
                return sb.toString().trim();
            }catch (Exception e) {
                if(progressdialog != null)
                {
                    progressdialog.dismiss();
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(String strJson)
        {
            if(strJson == null || strJson.length() <= 0)
            {
                Toast.makeText(getActivity(), "네트워크 상태를 확인해주십시오.", Toast.LENGTH_SHORT).show();
                if(progressdialog != null)
                {
                    progressdialog.dismiss();
                }
                return;
            }

            GetTotalCountAndRunRequest(strJson);
            if(progressdialog != null)
            {
                progressdialog.dismiss();
            }
        }
    }

    /*
     *
     *       전체 Data 개수를 추출해서 다시 Request를 보내 데이터를 받아온다.
     * */
    public void GetTotalCountAndRunRequest(String strJson)
    {
        try {
            JSONObject objJson = new JSONObject(strJson);
            JSONObject objData = objJson.getJSONObject("SearchConcertDetailService");
            int nCount = objData.getInt("list_total_count");

            String strRequestURL = mStrDefaultURL+Integer.toString(nCount);

            GetDataJSON objGetData = new GetDataJSON();
            objGetData.execute(strRequestURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
     *
     *       JSON 파싱 메소드
     *       이곳에서 리스트에 데이터를 삽입한다.
     * */
    public void MakeInfoArrayList(String strJson)
    {

        try {
            JSONObject objJson = new JSONObject(strJson);
            JSONObject objData = objJson.getJSONObject("SearchConcertDetailService");
            aJson = objData.getJSONArray("row");
            for(int i = 0; i < aJson.length(); i++)
            {
                //  적절한 데이터인지 필터링한다.
                JSONObject jsonobject = aJson.getJSONObject(i);
                if( mFilterData != null && IsValidData(jsonobject) == false)
                {
                    continue;
                }

                //  필터링한 결과에 들어가면 리스트에 추가.
                InfoDataType objInfo = new InfoDataType();
                objInfo.setStrCultCode(jsonobject.getString("CULTCODE"));
                objInfo.setStrSubjCode(jsonobject.getString("SUBJCODE"));
                objInfo.setStrCodeName(jsonobject.getString("CODENAME"));

                //  HTML 특수문자 치환
                String strTitle = ReplaceTitle(jsonobject.getString("TITLE"));
                objInfo.setStrTitle(strTitle);

                objInfo.setStrStartDate(jsonobject.getString("STRTDATE"));
                objInfo.setStrEndDate(jsonobject.getString("END_DATE"));
                objInfo.setStrTime(jsonobject.getString("TIME"));
                objInfo.setStrPlace(jsonobject.getString("PLACE"));
                objInfo.setStrOrgLink(jsonobject.getString("ORG_LINK"));

                //  대소문자 가리는 URL 때문에 도메인 소문자로 변경.
                String strImgUrl = jsonobject.getString("MAIN_IMG");
                int iCursor = strImgUrl.indexOf(":");               //  http:// 의 :를 찾기 위함
                iCursor += 3;

                //  도메인 부분만 잘라서 소문자화한 뒤, 다시 합쳐서 저장.
                String strDomain = "";
                strDomain += strImgUrl.substring(0, iCursor);
                strImgUrl = strImgUrl.substring(iCursor, strImgUrl.length());
                iCursor = strImgUrl.indexOf("/");
                strDomain += strImgUrl.substring(0, iCursor);
                strDomain = strDomain.toLowerCase();
                String strUrl = strDomain + "/" +strImgUrl.substring(iCursor+1);

                objInfo.setStrMainImg(strUrl);

                objInfo.setStrUseTarget(jsonobject.getString("USE_TRGT"));
                objInfo.setStrUseFee(jsonobject.getString("USE_FEE"));
                objInfo.setStrSponsor(jsonobject.getString("SPONSOR"));
                objInfo.setStrInquiry(jsonobject.getString("INQUIRY"));
                objInfo.setStrIsFree(jsonobject.getString("IS_FREE"));
                objInfo.setStrTicket(jsonobject.getString("TICKET"));
                objInfo.setStrContents(jsonobject.getString("CONTENTS"));
                aListInfo.add(objInfo);
            }
            mAdapter = new InfoFragmentRecyclerViewAdapter(getActivity(), aListInfo);
            BusProvider.getInstance().register(this);
            BusProvider.getInstance().post(new PushEvent(aListInfo));
            // set on click event listener to list view
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    ((FoldingCell) view).toggle(false);
                    mAdapter.registerToggle(pos);
                }
            });
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //  필터링 데이터를 기반으로 데이터 거르기.
    public boolean IsValidData(JSONObject jsonobject)
    {
        try {

            //  Subject Code 필터링한다.
            if(aListFilter.contains(jsonobject.getString("SUBJCODE")) == false)
            {
                return false;
            }

            //  유,무료 필터 적용
            if(mFilterData.getiIsFee() == 0 && jsonobject.getString("IS_FREE").equals("0"))
            {
                return false;
            }
            else if(mFilterData.getiIsFee() == 1 && jsonobject.getString("IS_FREE").equals("1"))
            {
                return false;
            }

            //  검색어 필터링
            String strFilterSearch = mFilterData.getStrSearch();
            if(!(strFilterSearch == null || strFilterSearch.length() <= 0))
            {
                if(jsonobject.getString("TITLE").contains(strFilterSearch) == false)
                {
                    return false;
                }
            }

            //  날짜 필터링 (YYYYMMDD)
            if(mFilterData.getStrDate_start() == null || mFilterData.getStrDate_start().length() <= 0)
            {
                return true;
            }

            if(mFilterData.getStrDate_end() == null || mFilterData.getStrDate_end().length() <= 0)
            {
                return true;
            }

            String strTempStartDate = jsonobject.getString("STRTDATE");
            String strTempEndDate = jsonobject.getString("END_DATE");

            strTempStartDate = strTempStartDate.replace("-", "");
            strTempEndDate = strTempEndDate.replace("-", "");
            if(!((strTempStartDate.compareTo(mFilterData.getStrDate_start()) >= 0 && strTempStartDate.compareTo(mFilterData.getStrDate_end()) <= 0)
                    || (strTempEndDate.compareTo(mFilterData.getStrDate_start()) >= 0 && strTempEndDate.compareTo(mFilterData.getStrDate_end()) <= 0)))
            {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    //  HTML 특수문자 치환
    public String ReplaceTitle(String strInputTitle)
    {
        String strTitle = strInputTitle;
        for(int i = 0; i < arrStrNew.length; i++)
        {
            strTitle = strTitle.replace(arrStrOld[i], arrStrNew[i]);
        }

        return strTitle;
    }

}
