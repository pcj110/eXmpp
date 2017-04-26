package com.cn.lx.entries;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by xueliang on 2017/4/9.
 */

public class SectionUserInfoEntrie extends SectionEntity {
    private UserInfoEntrie userInfo;
    public SectionUserInfoEntrie(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public UserInfoEntrie getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoEntrie userInfo) {
        this.userInfo = userInfo;
    }
}
