package com.SSUAndroidProject.fairy;

import com.SSUAndroidProject.fairy.DataType.InfoDataType;

import java.util.ArrayList;

/**
 * Created by 박PC on 2017-11-08.
 */

public class PushEvent {

    private ArrayList<InfoDataType> aInfoData;
    private UserInfo userinfo;


    public PushEvent(ArrayList<InfoDataType> aInfoData)
    {
        this.aInfoData = aInfoData;
    }
    public PushEvent(UserInfo userinfo){this.userinfo=userinfo;}


    /**
     * @return the pushlist
     */
    public ArrayList<InfoDataType> getList()
    {
        return aInfoData;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

}
