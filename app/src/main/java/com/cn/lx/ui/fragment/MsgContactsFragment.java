package com.cn.lx.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.activity.ChatActivity;
import com.cn.lx.util.UserInfoUtil;
import com.cn.lx.xmpp.XmppManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import estar.com.xmpptest.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xueliang on 2017/4/9.
 */

public class MsgContactsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msgcontact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Observable.create(new ObservableOnSubscribe<List<UserInfoEntrie>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserInfoEntrie>> e) throws Exception {
                XmppManager manager = XmppManager.getXmppManager();
                List<UserInfoEntrie> list = new ArrayList<UserInfoEntrie>();
                Cursor cursor =DataSupport.findBySQL("select userName,count(*),message from messageentrie   group by userName order by createTime desc");
                if (cursor!=null) {
                    for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                        String userName = cursor.getString(0);
                        String msgCount = cursor.getString(1);
                        String lastMsg = cursor.getString(2);
                        UserInfoEntrie userInfoEntrie =  manager.getClient().getUserInfo(userName);
                        userInfoEntrie.setLastMsg(lastMsg);
                        userInfoEntrie.setMsgCount(msgCount);
                        list.add(userInfoEntrie);
                    }
                    cursor.close();
                    cursor = null;
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<UserInfoEntrie>>() {

            @Override
            public void accept(final List<UserInfoEntrie> o) throws Exception {
                BaseQuickAdapter adapter = null;
                recyclerView.setAdapter(adapter = new BaseQuickAdapter<UserInfoEntrie,BaseViewHolder>(R.layout.item_msgcontact_list,o) {

                    @Override
                    protected void convert(BaseViewHolder helper, UserInfoEntrie item) {
                        helper.setText(R.id.userName,item.getNiceName());
                        helper.setText(R.id.dialogLastMessage,item.getLastMsg());
                        helper.setText(R.id.dialogUnreadBubble,item.getMsgCount());
                        if (TextUtils.isEmpty(item.getMsgCount())) {
                            helper.getView(R.id.dialogUnreadBubble).setVisibility(View.GONE);
                        }
                        Glide.with(MsgContactsFragment.this).load(R.drawable.touxiang).fitCenter().into((ImageView) helper.getView(R.id.avatar));
                        UserInfoUtil.loadAvatar(getActivity(),(ImageView) helper.getView(R.id.avatar),item.getUserName());

                    }
                });

                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("userInfo",o.get(position));
                        startActivity(intent);
                    }
                });
            }

        });

    }


}
