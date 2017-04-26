package com.cn.lx.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cn.lx.xmpp.XmppManager;

import java.io.File;

import estar.com.xmpptest.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xueliang on 2017/4/25.
 */

public class UserInfoUtil {

    public static void loadAvatar(final Context context, final ImageView imageView, final String userName){
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String str = XmppManager.getXmppManager().getClient().getAvatar(userName);
                e.onNext(str);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                File file = new File(o);
                if (file.exists()) {
                    Glide.with(context).load(file).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                }

            }
        });
    }



}
