package com.cn.lx.entries;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by xueliang on 2017/4/14.
 */

public class NewFriendEntrie extends DataSupport implements Parcelable{
    @Column(unique = true, defaultValue = "unknown")
    private String userName;
    private boolean isAdd;
    private Date createDate;

    public NewFriendEntrie(String userName, boolean isAdd, Date createDate) {
        this.userName = userName;
        this.isAdd = isAdd;
        this.createDate = createDate;
    }

    protected NewFriendEntrie(Parcel in) {
        userName = in.readString();
        isAdd = in.readByte() != 0;
    }

    public static final Creator<NewFriendEntrie> CREATOR = new Creator<NewFriendEntrie>() {
        @Override
        public NewFriendEntrie createFromParcel(Parcel in) {
            return new NewFriendEntrie(in);
        }

        @Override
        public NewFriendEntrie[] newArray(int size) {
            return new NewFriendEntrie[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeByte((byte) (isAdd ? 1 : 0));
    }
}
