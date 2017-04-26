package com.cn.lx.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import estar.com.xmpptest.R;

/**
 * Created by xueliang on 2017/4/13.
 */

public class NotifyManager {

    public static void notifyMsg(Context context,String title,String content){
        notifyMsg(context,title,content,new Intent());
    }
    public static void notifyMsg(Context context,String title,String content,Intent intent){
        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setDefaults(Notification.DEFAULT_ALL);
        notificationCompat.setContentTitle(title);
        notificationCompat.setContentText(content);
        notificationCompat.setSmallIcon(R.drawable.ic_luncher);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationCompat.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(1,notification);
    }
}
