package com.SSUAndroidProject.fairy.DataType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 박PC on 2017-11-05.
 */

public class ReviewDataType implements Parcelable{

    private String strDate;
    private String strTitle;
    private String strMainText;
    private String strUserEmail;
    private String strUsername;
    private String strKey;
    private float Star;
    private String strFestivalName;
    private String Location;
    private String strFestivalImg;
    private String strFestivalDate;
    private String strUserProfile;
    private boolean privacy;


    protected ReviewDataType(Parcel in) {
        strKey = in.readString();
        strUsername = in.readString();
        strDate = in.readString();
        strTitle = in.readString();
        strMainText = in.readString();
        strUserEmail = in.readString();
        Star = in.readFloat();
        privacy = in.readByte() != 0;
        strFestivalName = in.readString();
        strFestivalImg = in.readString();
        Location=in.readString();
        strFestivalDate=in.readString();
        strUserProfile=in.readString();

    }
    public void CopyData(ReviewDataType data){
        this.strKey=data.getStrKey();
        this.strUsername =data.getStrUsername();
        this.strDate = data.getStrDate();
        this.strTitle = data.getStrTitle();
        this.strMainText = data.getStrMainText();
        this.strUserEmail = data.getStrUserEmail();
        this.Star=data.getStar();
        this.privacy = data.isPrivacy();
        this.strFestivalName=data.getStrFestivalName();
        this.strFestivalImg=data.getStrFestivalImg();
        this.Location=data.getLocation();
        this.strFestivalDate=data.getStrFestivalDate();
        this.strUserProfile=data.getStrUserProfile();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strKey);
        dest.writeString(strUsername);
        dest.writeString(strDate);
        dest.writeString(strTitle);
        dest.writeString(strMainText);
        dest.writeString(strUserEmail);
        dest.writeFloat(Star);
        dest.writeByte((byte) (privacy ? 1 : 0));
        dest.writeString(strFestivalName);
        dest.writeString(strFestivalImg);
        dest.writeString(Location);
        dest.writeString(strFestivalDate);
        dest.writeString(strUserProfile);

    }
    public static final Creator<ReviewDataType> CREATOR = new Creator<ReviewDataType>() {
        @Override
        public ReviewDataType createFromParcel(Parcel in) {
            return new ReviewDataType(in);
        }

        @Override
        public ReviewDataType[] newArray(int size) {
            return new ReviewDataType[size];
        }
    };

    public String getStrUserProfile() {
        return strUserProfile;
    }

    public void setStrUserProfile(String strUserProfile) {
        this.strUserProfile = strUserProfile;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getStrDate() {
        return strDate;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrMainText() {
        return strMainText;
    }


    public String getStrUserEmail() {
        return strUserEmail;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setStrMainText(String strMainText) {
        this.strMainText = strMainText;
    }

    public void setStrUserEmail(String strUserEmail) {
        this.strUserEmail = strUserEmail;
    }

    public String getStrUsername() {
        return strUsername;
    }

    public void setStrUsername(String strUsername) {
        this.strUsername = strUsername;
    }

    public String getStrKey() {
        return strKey;
    }

    public void setStrKey(String strKey) {
        this.strKey = strKey;
    }

    public float getStar() {
        return Star;
    }

    public void setStar(float star) {
        Star = star;
    }

    public String getStrFestivalName() {
        return strFestivalName;
    }

    public void setStrFestivalName(String strFestivalName) {
        this.strFestivalName = strFestivalName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getStrFestivalImg() {
        return strFestivalImg;
    }

    public void setStrFestivalImg(String strFestivalImg) {
        this.strFestivalImg = strFestivalImg;
    }

    public String getStrFestivalDate() {
        return strFestivalDate;
    }

    public void setStrFestivalDate(String strFestivalDate) {
        this.strFestivalDate = strFestivalDate;
    }
    public ReviewDataType() {
    }
}
