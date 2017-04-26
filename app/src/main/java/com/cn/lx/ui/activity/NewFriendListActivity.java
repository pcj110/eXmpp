package com.cn.lx.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cn.lx.entries.NewFriendEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.util.UserInfoUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import estar.com.xmpptest.R;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class NewFriendListActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_list);
        ButterKnife.bind(this);
        initToolBar();
        getDefaultObservable(new ObservableOnSubscribe<List<NewFriendEntrie>>() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                List<NewFriendEntrie> newFriendEntrieList = DataSupport.findAll(NewFriendEntrie.class);
                e.onNext(newFriendEntrieList);
            }
        }).subscribe(new Consumer<List<NewFriendEntrie>>() {

            @Override
            public void accept(List<NewFriendEntrie> o) throws Exception {
                BaseQuickAdapter adapter = null;
                recyclerView.setLayoutManager(new LinearLayoutManager(NewFriendListActivity.this));
                recyclerView.setAdapter(adapter = new BaseQuickAdapter<NewFriendEntrie,BaseViewHolder>(R.layout.item_contact_list,o) {

                    @Override
                    protected void convert(BaseViewHolder helper, final NewFriendEntrie entrie) {
                        helper.setText(R.id.userName, entrie.getUserName());
                        Button view = helper.getView(R.id.rightBtn);
                        view.setVisibility(View.VISIBLE);
                        UserInfoUtil.loadAvatar(NewFriendListActivity.this,(ImageView) helper.getView(R.id.avatar),entrie.getUserName());
                        Glide.with(NewFriendListActivity.this).load(R.drawable.touxiang).centerCrop().into((ImageView) helper.getView(R.id.avatar));

                        if (manager.getClient().isAddUserByName(entrie.getUserName())) {
                            view.setText("已添加");
                            view.setEnabled(false);
                        }else{
                            view.setText("添加");
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (manager!=null) {
                                        manager.getClient().subcribePresence(entrie.getUserName());
                                        manager.getClient().addUser(getUserName(),entrie.getUserName(),null);
                                    }
                                }
                            });
                        }

                    }
                });
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        startActivity(UserInfoActivity.callingIntent(NewFriendListActivity.this,((NewFriendEntrie)adapter.getItem(position)).getUserName()));
                    }
                });
            }
        });
    }
}
