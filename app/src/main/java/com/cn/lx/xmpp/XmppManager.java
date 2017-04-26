package com.cn.lx.xmpp;

import com.cn.lx.entries.MessageEntrie;
import com.cn.lx.entries.UserInfoEntrie;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xueliang on 2017/3/14.
 */

public class XmppManager {

    private XmppClinet client;

    private static XmppManager xmppManager;


    public synchronized static  XmppManager getXmppManager(){

        if (xmppManager == null) {
            xmppManager = new XmppManager();
        }
        if (!xmppManager.client.isConnected()) {
            try {
                xmppManager.client.conn("zhuxl.com.cn",5222);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  xmppManager;
    }

    private XmppManager() {
        client = new XmppClientImp();
    }



    public XmppClinet getClient(){
        return client;
    }
}
