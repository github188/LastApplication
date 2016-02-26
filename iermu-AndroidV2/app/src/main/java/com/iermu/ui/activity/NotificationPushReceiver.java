package com.iermu.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.ui.util.BitmapUtil;

/**
 * Created by zhangxq on 16/1/20.
 */
public class NotificationPushReceiver extends BroadcastReceiver {

    public int mNewAlarmNotificationNum = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        //null || 不是推送通知广播
        if(intent==null) return;

        String action = intent.getAction();
        if(ErmuApplication.INTENT_NOTICATION_PUSH_CLEAR.equals(action)) {   //清除通知栏计数
            mNewAlarmNotificationNum = 1;
            //是否同时清除通知栏
        } else if(ErmuApplication.INTENT_NOTICATION_PUSH.equals(action)) {  //新的通知栏广播
            //String deviceId   = intent.getStringExtra("deviceId");
            String title        = intent.getStringExtra("title");
            String description  = intent.getStringExtra("description");
            NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            Intent notif = new Intent(context, MainActivity.class);
            notif.putExtra(MainActivity.INTENT_NEW_MESSAGE, "newMessage");
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, notif, PendingIntent.FLAG_UPDATE_CURRENT);
            // 通过Notification.Builder来创建通知，注意API Level
            // API11之后才支持
            boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
            int smaillIcon = useWhiteIcon ? R.drawable.notification_icon_trans : R.drawable.notification_icon;
            Notification notify2 = new Notification.Builder(context)
                    .setLargeIcon(BitmapUtil.drawableToBitmap(context, R.drawable.ic_launcher))
                    .setTicker(description)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(pendingIntent2)
                    .setNumber(mNewAlarmNotificationNum)
                    .setSmallIcon(smaillIcon)
                    .getNotification();
            notify2.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(1, notify2);
            mNewAlarmNotificationNum++;
        }
    }

}
