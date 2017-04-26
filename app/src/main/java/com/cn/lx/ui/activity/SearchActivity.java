package com.cn.lx.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.ui.fragment.MsgContactsFragment;
import com.cn.lx.xmpp.XmppManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import estar.com.xmpptest.R;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(null);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("搜索");
        searchView.setIconifiedByDefault(false);
        searchView.setBackgroundResource(android.support.v7.appcompat.R.drawable.abc_edit_text_material);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    return false;
                }
                searchUser(newText);
                return true;
            }
        });

        return true;
    }

    public void searchUser(final String userName){
        getDefaultObservable(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                XmppManager manager =   XmppManager.getXmppManager();
                e.onNext(manager.getClient().searchUsers(userName));
            }
        }).subscribe(new Consumer<List<String>>() {

            @Override
            public void accept(List<String> o) throws Exception {
                BaseQuickAdapter adapter = null;
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                recyclerView.setAdapter(adapter = new BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_msgcontact_list,o) {

                    @Override
                    protected void convert(BaseViewHolder helper, String string) {
                        helper.setText(R.id.userName, string);
                        Glide.with(SearchActivity.this).load(R.drawable.touxiang).centerCrop().into((ImageView) helper.getView(R.id.avatar));
                    }
                });
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        startActivity(new Intent(SearchActivity.this,UserInfoActivity.class));
                    }
                });
            }
        });
    }

}
