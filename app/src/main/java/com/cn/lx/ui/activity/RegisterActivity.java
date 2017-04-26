package com.cn.lx.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.xmpp.XmppManager;

import org.jivesoftware.smackx.iqregister.packet.Registration;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import estar.com.xmpptest.R;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.SubscriberCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity {

    XmppManager xmppManager;
    @BindView(R.id.userName)
    EditText userName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.nickName)
    EditText nickName;
    @BindView(R.id.avatar)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.registerBtn)
    void register(){
        if ("".equals(userName.getText().toString())) {
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(nickName.getText().toString())) {
            Toast.makeText(this,"请确认昵称",Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(password.getText().toString())) {
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }


        getDefaultObservable(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                XmppManager manager = XmppManager.getXmppManager();
                manager.getClient().register(userName.getText().toString(),password.getText().toString(),"");
                e.onNext("");
            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                dissmissDialog();
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                dissmissDialog();
                return;
            }
        });

    }

    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what ==0) {
                Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                return;
            }else{
                finish();
            }

        }
    };
}
