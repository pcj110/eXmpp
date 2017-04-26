package com.cn.lx.xmpp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cn.lx.common.NotifyManager;
import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.NewFriendEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.activity.ChatActivity;
import com.cn.lx.ui.activity.NewFriendListActivity;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xueliang on 2017/3/14.
 */

public class XmppService extends Service {
    private final static String TAG = "XmppService";

    public interface ChatMessageListerer{
        public void onInComingMsg(MessageEntrie messageEntrie);
        public void onOutComintMsg(MessageEntrie messageEntrie);
    }
    private XmppBind xmppBind = new XmppBind();

    public class XmppBind extends Binder{
        public XmppService getService(){
            return XmppService.this;
        }

    }

    private ChatMessageListerer messageListerer;
    private XmppManager manager;

    public void setMessageListener(ChatMessageListerer messageListener){
        this.messageListerer = messageListener;
        System.out.print("");
    }



    @Override
    public void onCreate() {
        Log.i(TAG,"xmppService onCreate");
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                 manager = XmppManager.getXmppManager();
                if (manager!=null){
                    e.onNext("");
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                manager.getClient().addChatListener(new IncomingChatMessageListener() {
                    @Override
                    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                        Log.i("111",from.getLocalpart()+"-----------"+message.getBody());
                        Intent intent = new Intent(XmppService.this, ChatActivity.class);
                        UserInfoEntrie userInfoEntrie = manager.getClient().getUserInfo(from.getLocalpart().toString());
                        intent.putExtra("userInfo",userInfoEntrie);
                        NotifyManager.notifyMsg(XmppService.this,from.getLocalpart().toString(),message.getBody(),intent);

                        MessageEntrie messageEntrie = new MessageEntrie();
                        messageEntrie.setUserName(from.getLocalpart().toString());
                        messageEntrie.setMessage(message.getBody());
                        messageEntrie.setFromUserName(messageEntrie.getUserName());
                        messageEntrie.saveAsync().listen(new SaveCallback() {
                            @Override
                            public void onFinish(boolean success) {

                            }
                        });
                        if (messageListerer!=null) {
                            messageListerer.onInComingMsg(messageEntrie);
                        }
                    }

                },null);
                manager.getClient().addPresenceEventAndSubscribeListener(new PresenceEventListener() {
                    @Override
                    public void presenceAvailable(FullJid address, Presence availablePresence) {

                    }

                    @Override
                    public void presenceUnavailable(FullJid address, Presence presence) {

                    }

                    @Override
                    public void presenceError(Jid address, Presence errorPresence) {

                    }

                    @Override
                    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {

                    }

                    @Override
                    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {

                    }
                }, new SubscribeListener() {
                    @Override
                    public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                        String fromUser = null;
                        if (from.hasLocalpart()) {
                            fromUser = from.getLocalpartOrNull().toString();
                        }else{
                            fromUser = from.getLocalpartOrNull().toString();
                        }

                        NotifyManager.notifyMsg(XmppService.this,"好友添加",fromUser+"请求添加你为好友",new Intent(XmppService.this, NewFriendListActivity.class));
                        List<NewFriendEntrie> newFriendEntrieList = DataSupport.where("userName = ?",fromUser).find(NewFriendEntrie.class);
                        if (newFriendEntrieList!=null&&newFriendEntrieList.size()>0) {
                            return null;
                        }
                        NewFriendEntrie newFriendEntrie  = new NewFriendEntrie(fromUser,false,new Date());
                        newFriendEntrie.save();
                        return null;
                    }
                });
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"xmppService onDestroy");

        if (manager!=null) {
            manager.getClient().addChatListener(null,null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return xmppBind;
    }
}
