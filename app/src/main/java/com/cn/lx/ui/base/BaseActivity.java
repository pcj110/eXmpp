package com.cn.lx.ui.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.cn.lx.common.Constans;
import com.cn.lx.common.SharedPreferencesManager;
import com.cn.lx.xmpp.XmppManager;

import estar.com.xmpptest.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xueliang on 2017/4/6.
 */

public class BaseActivity extends AppCompatActivity {

    private Dialog dialog;
    protected  XmppManager manager;

    public void manageInitSuccess(){

    }
    public boolean isDefaultToolBar(){
        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                manager = XmppManager.getXmppManager();
                if (manager!=null){
                    if (!manager.getClient().isAuthenticated()) {
//                        manager.login("","");
                    }
                }
                e.onNext("");
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                manageInitSuccess();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isDefaultToolBar()) {
            initToolBar();
        }
    }

    protected  void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back);//设置ToolBar头部图标
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    onKeyDown(KeyEvent.KEYCODE_BACK,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
                    onBackPressed();
                }
            });
        }

    }



    protected void showDilog(String message){
        dialog = ProgressDialog.show(this,null,message,true,false);
    }

    protected void dissmissDialog(){
        if (dialog!=null&&dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected Observable getDefaultObservable(ObservableOnSubscribe observableOnSubscribe){
        return Observable.create(observableOnSubscribe).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    protected void showMessageDialogBySure(String msg){
        new AlertDialog.Builder(this).setTitle("提示").setMessage(msg).setPositiveButton("确定",null).show();
    }

    public String getUserName(){
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        return sharedPreferencesManager.getStringByKey(Constans.USER_NAME);
    }
}
