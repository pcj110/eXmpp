package com.cn.lx.entries;

import android.os.Parcel;
import android.os.Parcelable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by xueliang on 2017/4/9.
 */

public class MessageEntrie  extends DataSupport implements IMessage,Parcelable {

    private String userName;
    private String message;
    private String toUserName;
    private String fromUserName;
    private Date createTime = new Date();
    private String isRead = "0";//0 未读 ，1 已读


    public MessageEntrie(){}
    protected MessageEntrie(Parcel in) {
        userName = in.readString();
        message = in.readString();
        toUserName = in.readString();
        fromUserName = in.readString();
        createTime = new Date(in.readLong());
        isRead = in.readString();
    }

    public static final Creator<MessageEntrie> CREATOR = new Creator<MessageEntrie>() {
        @Override
        public MessageEntrie createFromParcel(Parcel in) {
            return new MessageEntrie(in);
        }

        @Override
        public MessageEntrie[] newArray(int size) {
            return new MessageEntrie[size];
        }
    };

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getId() {
        return userName;
    }

    @Override
    public String getText() {
        return message;
    }

    @Override
    public IUser getUser() {
        UserInfoEntrie userInfoEntrie  = new UserInfoEntrie();
        userInfoEntrie.setUserName(fromUserName);
        return userInfoEntrie;
    }

    @Override
    public Date getCreatedAt() {
        return new Date();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(message);
        parcel.writeString(toUserName);
        parcel.writeString(fromUserName);
        parcel.writeLong(createTime.getTime());
        parcel.writeString(isRead);
    }
}
