package com.SSUAndroidProject.fairy.DataType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 박PC on 2017-11-18.
 */

public class CommentDataType implements Parcelable {

    private String  username;
    private String userprofile;
    private String Comment;
    private String Date;

    public CommentDataType() {
    }

    protected CommentDataType(Parcel in) {
        username=in.readString();
        userprofile=in.readString();
        Comment = in.readString();
        Date=in.readString();
    }

    public static final Creator<CommentDataType> CREATOR = new Creator<CommentDataType>() {
        @Override
        public CommentDataType createFromParcel(Parcel in) {
            return new CommentDataType(in);
        }

        @Override
        public CommentDataType[] newArray(int size) {
            return new CommentDataType[size];
        }
    };

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(userprofile);
        dest.writeString(Comment);
        dest.writeString(Date);
    }
}
