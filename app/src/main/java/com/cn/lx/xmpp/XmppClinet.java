package com.cn.lx.xmpp;

import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.UserInfoEntrie;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.SubscribeListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xueliang on 2017/3/14.
 */

public interface XmppClinet {

    public void conn(String host,int port) throws Exception;
    public void login(String userName,String passWord) throws XMPPException,SmackException,IOException,InterruptedException;
    public boolean register(String userName,String password,String nickName);
    public void loginOut();
    public boolean isConnected();

    public boolean isAuthenticated();
    public List<UserInfoEntrie> getAllEntries();
    public boolean sendMsg(MessageEntrie messageEntrie);
    public  void addChatListener(IncomingChatMessageListener incomingChatMessageListener, OutgoingChatMessageListener outgoingChatMessageListener);
    public List<String> searchUsers(String userName);

    public void addUser(String userName,String toUser,String[]groups);
    public void addPresenceEventAndSubscribeListener(PresenceEventListener presenceEventListener, SubscribeListener subscribeListener);
    public boolean subcribePresence(String formUser);
    public UserInfoEntrie getUserInfo(String userName);

    public boolean changeAvator(byte [] bt);
    public boolean saveUserInfo(UserInfoEntrie userInfoEntrie);
    public String getAvatar(String userName);
    public boolean isAddUserByName(String userName);
}
