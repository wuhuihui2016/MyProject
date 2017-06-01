package com.fengyang.music.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import com.fengyang.music.R;
import com.fengyang.music.service.PlayService;
import com.fengyang.toollib.utils.ActivityUtils;
import com.fengyang.toollib.utils.LogUtils;

import java.io.IOException;

/**
 * @Title: NotificationUtils
 * @Description: TODO 通知工具类
 * @author wuhuihui
 * @date 2016年5月16日 下午4:19:26 
 */
public class NotificationUtils {

	public static final String TAG = "NotificationUtils";

	public static final String ACTION_PLAY = "Notify.play";//播放
	public static final String ACTION_NEXT = "Notify.next";//下一首
	public static final String ACTION_PAUSE = "Notify.pause";//暂停
	public static final String ACTION_CLOSE = "Notify.close";//暂停

	public static NotificationManager notifyManager;
	public static int requestCode = 0;

	/**
	 * @Title: updateNotification
	 * @Description: TODO 更新通知显示信息
	 * @param context
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月16日 下午4:18:56
	 */
	public static void updateNotification(Context context) {

		Intent playIntent = new Intent();

		notifyManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.icon_app_notify;
		notification.when = System.currentTimeMillis();
		notification.tickerText = "懒猫音乐";

		//通知图文布局
		//注意: remoteViews布局中有控件的限定，且不可有onClick属性
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_notify_layout);

		if (MusicUtils.getLastMusic() != null) {
			notification.tickerText = MusicUtils.getLastMusic().getTitle();
			remoteViews.setTextViewText(R.id.song, MusicUtils.getLastMusic().getTitle());
			remoteViews.setTextViewText(R.id.singer, MusicUtils.getLastMusic().getArtist());
		} else {
			remoteViews.setTextViewText(R.id.song, "听音乐");
			remoteViews.setTextViewText(R.id.singer, "享现在");
		}

		if (PlayService.isPlaying()) {
			remoteViews.setImageViewResource(R.id.play, R.drawable.btn_pause_press);

			playIntent.setAction(ACTION_PAUSE);
			remoteViews.setOnClickPendingIntent(R.id.play, PendingIntent.getBroadcast(
					context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));

		} else {
			remoteViews.setImageViewResource(R.id.play, R.drawable.btn_play_press);

			playIntent.setAction(ACTION_PLAY);
			remoteViews.setOnClickPendingIntent(R.id.play, PendingIntent.getBroadcast(
					context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		}

		playIntent.setAction(ACTION_NEXT);
		remoteViews.setOnClickPendingIntent(R.id.next, PendingIntent.getBroadcast(
				context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));

		playIntent.setAction(ACTION_CLOSE);
		remoteViews.setOnClickPendingIntent(R.id.close, PendingIntent.getBroadcast(
				context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT));

		//仅在除PlayActivity页面外的通知有全局点击效果
		LogUtils.i(TAG, "currentActivity---" +  ActivityUtils.currentActivity().getLocalClassName());
		if (! ActivityUtils.currentActivity().getLocalClassName().contains("PlayActivity")) {
			Intent notifyIntent = new Intent(Intent.ACTION_MAIN);
			notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			notifyIntent.setComponent(new ComponentName(context.getPackageName(),
					//				context.getPackageName() + "." + ActivityUtils.currentActivity().getLocalClassName()
					"com.fengyang.music.activity.PlayActivity"
			));
			notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(
					context, requestCode, notifyIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			notification.contentIntent = pendingIntent;//设置通知点击事件的意图
		}

		notification.flags = Notification.FLAG_NO_CLEAR;//设置通知不可清除，且能在被杀掉进程后自动清除
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.contentView = remoteViews;
		notifyManager.notify(requestCode, notification);

	}

	/**
	 * @Title: cancel
	 * @Description: TODO  取消通知
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月17日 下午2:17:30
	 */
	public static void cancel() {
		if (notifyManager != null) {
			notifyManager.cancel(requestCode);
		}
	}

	/**
	 * 避免某些手机安装app后自动屏蔽通知等权限，比如oppo，
	 * 通知声音不出时可选择播放系统通知音，并设置音量到最大
	 * @param context
     */
	public static void playSound (Context context) {

		try {
			//设置音量最大
			AudioManager am=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			am.setStreamVolume(AudioManager.STREAM_MUSIC,
					am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
			am.setStreamVolume(AudioManager.STREAM_RING,
					am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);

			Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
			String originfilePath = "";
			String[] filePathColumn = { MediaStore.Images.Media.DATA};
			Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
			if(cursor != null){
				if(cursor.moveToFirst()){
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					originfilePath = cursor.getString(columnIndex);
				}
				cursor.close();
			}
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(originfilePath);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
