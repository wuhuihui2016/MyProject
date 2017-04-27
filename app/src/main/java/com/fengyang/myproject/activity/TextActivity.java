package com.fengyang.myproject.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.StringUtils;

import java.util.Date;

/**
 * 图片功能的结合页面
 * 显示GIF图片
 * SDcard和相机权限打开后
 * 1.下载并显示图片（标志isDownload = true）
 * 2.拍照后返回图片显示
 *
 */
public class TextActivity extends BaseActivity implements View.OnClickListener {

    private NotificationManager notifyManager;
    private int i = 0;//通知编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("文字游戏", R.layout.activity_text);

        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    //简易通知
    private void notify1() {
        i ++ ;
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int requestCode = 1;
        Notification notification = new Notification();
        notification.icon = R.mipmap.app_icon;
        notification.when = System.currentTimeMillis();
        notification.tickerText = "简易通知" + i;
        //通知图文布局
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_layout);
        remoteViews.setTextViewText(R.id.name, "简易通知" + i);
        remoteViews.setTextColor(R.id.name, Color.parseColor("#454545"));
        remoteViews.setTextViewText(R.id.time, StringUtils.format(new Date()));
        remoteViews.setImageViewResource(R.id.image, R.mipmap.app_icon);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("data", "" + i);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), requestCode, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击后消失
        notification.defaults = Notification.DEFAULT_ALL;
        notification.contentIntent = pendingIntent;//设置通知点击事件的意图
        notification.contentView = remoteViews;
        notifyManager.cancel(1);
        notifyManager.notify(requestCode, notification);
    }

    //含通知图文布局
    private void notify2() {
        i ++;
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int requestCode = 2;
        Notification notification = new Notification();
        notification.icon = R.mipmap.app_icon;
        notification.when = System.currentTimeMillis();
        notification.tickerText = "自定义View已经启动";
        //通知图文布局
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_layout);
        remoteViews.setTextViewText(R.id.name, "自定义View");
        remoteViews.setTextViewText(R.id.time, StringUtils.format(new Date()));
        remoteViews.setImageViewResource(R.id.image, R.mipmap.app_icon);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("data", "" + i);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), requestCode, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        //		notification.setLatestEventInfo(getApplicationContext(),"自定义View", "", pendingIntent);//设置通知点击事件的意图(已过时)
        notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击后消失
        notification.defaults = Notification.DEFAULT_ALL;
        notification.contentView = remoteViews;
        notification.contentIntent = pendingIntent;//设置通知点击事件的意图
        notifyManager.cancel(2);//取消简易通知
        notifyManager.notify(requestCode, notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notify1:
                notify1();
                break;
            case R.id.notify2:
                notify2();
                break;
        }
    }


}
