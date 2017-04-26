package com.cn.lx.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;

import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.xmpp.XmppService;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.litepal.crud.callback.SaveCallback;

import estar.com.xmpptest.R;

public class ChatActivity extends BaseActivity {

    private static final byte CONTENT_TYPE_VOICE = 1;


    protected MessagesListAdapter<MessageEntrie> messagesAdapter;
    private MessagesList messagesList;

    private ServiceConnection connection;

    private UserInfoEntrie userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userInfo = getIntent().getParcelableExtra("userInfo");
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();
        setTitle(userInfo.getNiceName());
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                submit(input);
                return true;
            }
        });
        input.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, XmppService.class), connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                XmppService.XmppBind binder = (XmppService.XmppBind) iBinder;

                binder.getService().setMessageListener(new XmppService.ChatMessageListerer() {
                    @Override
                    public void onInComingMsg(final MessageEntrie messageEntrie) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messagesAdapter.addToStart(messageEntrie,true);
                            }
                        });

                    }

                    @Override
                    public void onOutComintMsg(MessageEntrie messageEntrie) {

                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connection!=null) {
            unbindService(connection);
        }
    }

    public boolean submit(CharSequence input) {
        MessageEntrie messageEntrie = new MessageEntrie();
        messageEntrie.setUserName(userInfo.getUserName());
        messageEntrie.setMessage(input.toString());
        messageEntrie.setToUserName(userInfo.getUserName());
        messageEntrie.setFromUserName(getUserName());
        messagesAdapter.addToStart(messageEntrie,true);
        manager.getClient().sendMsg(messageEntrie);
        messageEntrie.saveAsync().listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {

            }
        });
//        messagesAdapter.addToStart(
//                MessagesFixtures.getTextMessage(input.toString()), true);
        return true;
    }



    private void initAdapter() {
//        MessageHolders holders = new MessageHolders()
//                .registerContentType(
//                        CONTENT_TYPE_VOICE,
//                        IncomingVoiceMessageViewHolder.class,
//                        R.layout.item_custom_incoming_voice_message,
//                        OutcomingVoiceMessageViewHolder.class,
//                        R.layout.item_custom_outcoming_voice_message,
//                        this);


        messagesAdapter = new MessagesListAdapter<MessageEntrie>(getUserName(), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {

            }
        });
//        messagesAdapter.enableSelectionMode(this);
//        messagesAdapter.setLoadMoreListener(this);
        this.messagesList.setAdapter(messagesAdapter);
    }
}
