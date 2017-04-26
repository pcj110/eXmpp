package com.cn.lx.entries;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.stfalcon.chatkit.commons.models.IUser;

import org.litepal.crud.DataSupport;

/**
 * Created by xueliang on 2017/4/9.
 */

public class UserInfoEntrie extends DataSupport implements Parcelable,IUser {
    private String userName;//
    private String niceName;
    private String phone;
    private String email;

    private String avatar;//头像
    private String sex;
    private String describe;
    private String area;

    private String lastMsg;
    private String msgCount;


    public UserInfoEntrie(){}

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getNiceName() {
        if (TextUtils.isEmpty(niceName)) {
            return userName;
        }
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    protected UserInfoEntrie(Parcel in) {
        userName = in.readString();
        niceName = in.readString();
        phone = in.readString();
        email = in.readString();
        avatar = in.readString();
        sex = in.readString();
        describe = in.readString();
        area = in.readString();
        lastMsg = in.readString();
        msgCount = in.readString();

    }

    public static final Creator<UserInfoEntrie> CREATOR = new Creator<UserInfoEntrie>() {
        @Override
        public UserInfoEntrie createFromParcel(Parcel in) {
            return new UserInfoEntrie(in);
        }

        @Override
        public UserInfoEntrie[] newArray(int size) {
            return new UserInfoEntrie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.userName);
        parcel.writeString(this.niceName);
        parcel.writeString(this.phone);
        parcel.writeString(this.email);
        parcel.writeString(this.avatar);
        parcel.writeString(this.sex);
        parcel.writeString(this.describe);
        parcel.writeString(this.area);
        parcel.writeString(this.lastMsg);
        parcel.writeString(this.msgCount);
    }

    @Override
    public String getId() {
        return userName;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
