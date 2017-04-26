package com.cn.lx.ui.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cn.lx.entries.SectionUserInfoEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.util.FileUtil;
import com.cn.lx.util.UserInfoUtil;

import java.io.File;
import java.util.List;

import estar.com.xmpptest.R;

public class SectionAdapter extends BaseSectionQuickAdapter<SectionUserInfoEntrie, BaseViewHolder> {

    public SectionAdapter(int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final SectionUserInfoEntrie item) {
        helper.setText(R.id.headtext,item.header);
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionUserInfoEntrie item) {
        UserInfoEntrie userInfoEntrie = item.getUserInfo();
        helper.setText(R.id.userName,userInfoEntrie.getNiceName());
        File file = new File(FileUtil.getCacheAvatarDir(), "avatar_" + userInfoEntrie.getUserName() + ".png");
        if (file.exists()) {
            Glide.with(helper.convertView.getContext()).load(file).into((ImageView) helper.getView(R.id.avatar));
        }else{
            Glide.with(helper.convertView.getContext()).load(R.drawable.touxiang).into((ImageView) helper.getView(R.id.avatar));
            UserInfoUtil.loadAvatar(helper.convertView.getContext(), (ImageView) helper.getView(R.id.avatar),userInfoEntrie.getUserName());
        }
    }
}
