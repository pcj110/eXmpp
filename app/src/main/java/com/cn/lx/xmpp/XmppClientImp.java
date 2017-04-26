package com.cn.lx.xmpp;

import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.util.FileUtil;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdatalayout.packet.DataLayout;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xueliang on 2017/3/14.
 */

public class XmppClientImp implements XmppClinet {
    private static AbstractXMPPConnection connection;

    private static final String describe = "describe";
    private static final String sex = "sex";
    private static final String area = "area";

    private VCardManager getVcardManager() {
        return VCardManager.getInstanceFor(connection);
    }

    private String getServerName() {
        return connection.getServiceName().toString();
    }

    @Override
    public void conn(String host, int port) throws Exception {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain("localhost")
//                .setHost("59.110.143.244")
//                .setHost("zhuxl.com.cn")
                .setHostAddress(InetAddress.getByName("zhuxl.com.cn"))
                .setPort(5223)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();

        AbstractXMPPConnection conn2 = new XMPPTCPConnection(config);
        connection = conn2.connect();

    }

    @Override
    public void login(String userName, String passWord) throws XMPPException, SmackException, IOException, InterruptedException {

        connection.login(userName, passWord);
        Roster roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);


    }

    @Override
    public boolean register(String userName, String password, String nickName) {
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {

            accountManager.sensitiveOperationOverInsecureConnection(true);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("name", nickName);
            accountManager.createAccount(Localpart.from(userName), password, attributes);
            return true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void loginOut() {
        if (connection != null) {

            connection.disconnect();
            connection = null;
            Log.i("111", "disconnect------");
        }

    }

    /**
     * 是否连接
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        if (connection != null) {
            return connection.isConnected();
        }
        return false;
    }

    /**
     * 是否登录
     *
     * @return
     */
    @Override
    public boolean isAuthenticated() {
        if (connection != null) {
            return connection.isAuthenticated();
        }
        return false;
    }

    /**
     * 获取所有好友信息
     *
     * @return
     */
    public List<UserInfoEntrie> getAllEntries() {
        if (connection == null)
            return null;
        List<UserInfoEntrie> userInfoEntrieList = new ArrayList<UserInfoEntrie>();
        Roster roster = Roster.getInstanceFor(connection);
        Iterator<RosterEntry> i = roster.getEntries().iterator();

        while (i.hasNext()) {
            RosterEntry rosterEntry = i.next();
            String userName = rosterEntry.getJid().asEntityBareJidIfPossible().getLocalpart().toString();
            userInfoEntrieList.add(getUserInfo(userName));
        }
        return userInfoEntrieList;
    }

    @Override
    public boolean sendMsg(MessageEntrie messageEntrie) {
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(messageEntrie.getToUserName() + "@" + getServerName());
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        if (jid != null) {
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.chatWith(jid);
            try {
                Message message = new Message(jid, messageEntrie.getMessage());
                message.setType(Message.Type.chat);
                chat.send(message);
                return true;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("11", "jid is null");
        }
        return false;
    }

    public void addChatListener(IncomingChatMessageListener incomingChatMessageListener, OutgoingChatMessageListener outgoingChatMessageListener) {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        if (incomingChatMessageListener != null) {
            chatManager.addIncomingListener(incomingChatMessageListener);
        }
        if (outgoingChatMessageListener != null) {
            chatManager.addOutgoingListener(outgoingChatMessageListener);
        }
    }

    /**
     * 查询用户
     *
     * @param userName
     * @return
     * @throws XMPPException
     */
    public List<String> searchUsers(String userName) {
        List<String> results = new ArrayList<String>();

        if (connection == null)
            return results;
        try {

            UserSearchManager usm = new UserSearchManager(connection);

            Form searchForm = usm.getSearchForm(JidCreate.domainBareFrom("search." + connection.getServiceName()));
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = usm.getSearchResults(answerForm, JidCreate.domainBareFrom("search." + connection.getServiceName()));
            List<ReportedData.Row> list = data.getRows();
            for (ReportedData.Row row : list) {
                results.add(row.getValues("Username").get(0));
            }
            return results;
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public void addUser(String userName, String toUser, String[] groups) {
        Roster roster = Roster.getInstanceFor(connection);
        roster.addPresenceEventListener(new PresenceEventListener() {
            @Override
            public void presenceAvailable(FullJid address, Presence availablePresence) {
                Log.i("rost", "presenceAvailable");
            }

            @Override
            public void presenceUnavailable(FullJid address, Presence presence) {
                Log.i("rost", "presenceUnavailable");
            }

            @Override
            public void presenceError(Jid address, Presence errorPresence) {
                Log.i("rost", "presenceAvailable");
            }

            @Override
            public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
                Log.i("rost", "presenceSubscribed");
            }

            @Override
            public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
                Log.i("rost", "presenceUnsubscribed");
            }
        });
        roster.addSubscribeListener(new SubscribeListener() {
            @Override
            public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                Log.i("rost", "processSubscribe");
                return null;
            }
        });
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {

            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {

            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {

            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });
        roster.addRosterLoadedListener(new RosterLoadedListener() {
            @Override
            public void onRosterLoaded(Roster roster) {

            }

            @Override
            public void onRosterLoadingFailed(Exception exception) {

            }
        });
        try {
            BareJid jid = JidCreate.bareFrom(userName + "@localhost");
            try {
                roster.createEntry(jid, toUser, groups);
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public void addPresenceEventAndSubscribeListener(PresenceEventListener presenceEventListener, SubscribeListener subscribeListener) {
        Roster roster = Roster.getInstanceFor(connection);
        roster.addPresenceEventListener(presenceEventListener);
        roster.addSubscribeListener(subscribeListener);

    }

    public boolean subcribePresence(String formUser) {
        Presence presence = new Presence(Presence.Type.subscribe);
        try {
            presence.setTo(JidCreate.entityBareFrom(formUser + "@localhost"));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        try {
            connection.sendStanza(presence);
            return true;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAvatar(String userName) {
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        try {
            VCard vCard = vCardManager.loadVCard(JidCreate.entityBareFrom(userName + "@" + getServerName()));

            vCard.getAvatar();
            byte[] bt = vCard.getAvatar();
            if (bt!=null) {
                File file = FileUtil.getFile(bt, FileUtil.getCacheAvatarDir(), "avatar_" + userName + ".png");
                return file.getAbsolutePath();
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取用户信息
     *
     * @param userName 为空则获取当前登陆人的信息
     * @return
     */
    public UserInfoEntrie getUserInfo(String userName) {
        VCard vCard = null;
        try {
            UserInfoEntrie userInfoEntrie = new UserInfoEntrie();

            if (TextUtils.isEmpty(userName)) {
                vCard = getVcardManager().loadVCard();
                userInfoEntrie.setUserName(userName = connection.getUser().getLocalpart().toString());

            } else {
                vCard = getVcardManager().loadVCard(JidCreate.entityBareFrom(userName + "@" + getServerName()));
                userInfoEntrie.setUserName(userName);
            }
            userInfoEntrie.setNiceName(vCard.getFirstName());
            userInfoEntrie.setDescribe(vCard.getField(describe));
            userInfoEntrie.setSex(vCard.getField(sex));
            userInfoEntrie.setArea(vCard.getField(area));

            return userInfoEntrie;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean saveUserInfo(UserInfoEntrie userInfoEntrie) {
        VCard vCard = null;
        try {
            vCard = getVcardManager().loadVCard(JidCreate.entityBareFrom(userInfoEntrie.getUserName() + "@" + getServerName()));
            if (!TextUtils.isEmpty(userInfoEntrie.getNiceName())) {
                vCard.setFirstName(userInfoEntrie.getNiceName());
            }
            if (!TextUtils.isEmpty(userInfoEntrie.getDescribe())) {
                vCard.setField(describe,userInfoEntrie.getDescribe());
            }
            if (!TextUtils.isEmpty(userInfoEntrie.getSex())) {
                vCard.setField(sex,userInfoEntrie.getSex());
            }
            if (!TextUtils.isEmpty(userInfoEntrie.getArea())) {
                vCard.setField(area,userInfoEntrie.getArea());
            }
            getVcardManager().saveVCard(vCard);
            return true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeAvator(byte[] bt) {

        try {
            VCard vcard = getVcardManager().loadVCard();
            vcard.setAvatar(bt);
            getVcardManager().saveVCard(vcard);
            return true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAddUserByName(String userName){
        Roster roster = Roster.getInstanceFor(connection);
        try {
          return   roster.isSubscribedToMyPresence(JidCreate.entityBareFrom(userName+ "@" + getServerName()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return false;
    }
}
