package com.fengyang.music.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.util.Log;

import com.fengyang.music.model.Music;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.NotificationUtils;
import com.fengyang.music.utils.ToolUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description: TODO 连接播放器的Service
 * @author: wuhuihui
 * @time: 2015年11月4日 下午3:26:30
 */
public class PlayService extends Service {

	private static final String TAG = "PlayService";

	public static MediaPlayer media = null;//音乐播放媒体

	public static final String ACTION_PLAY = "play";//播放
	public static final String ACTION_PRE = "pre";//上一首
	public static final String ACTION_NEXT = "next";//下一首
	public static final String ACTION_PAUSE = "pause";//暂停

	public static final String ACTION_SETTIME = "settime";//设置睡眠时间
	public static final String ACTION_TOEXIT = "toexit";//即将退出
	public static final String ACTION_EXIT = "exit";//退出

	private MyNotifyReceiver notifyReceiver;//通知的指令
	private ScreenReceiver screenReceiver;//锁屏接收器

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "onCreate");
		//注册通知接收器
		notifyReceiver = new MyNotifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NotificationUtils.ACTION_PLAY);
		filter.addAction(NotificationUtils.ACTION_NEXT);
		filter.addAction(NotificationUtils.ACTION_PAUSE);
		filter.addAction(NotificationUtils.ACTION_CLOSE);
		registerReceiver(notifyReceiver, filter);

		//注册锁屏接收器
		screenReceiver = new ScreenReceiver();
		IntentFilter screenFilter = new IntentFilter();
		screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, screenFilter);

		ContansUtils.getMusicList(getApplicationContext());//获取所有音乐列表
		ContansUtils.getEditor(getApplicationContext());//获取app存储空间
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			Log.i(TAG, "OnReceiver---" + intent.getAction());
			if (intent.getAction() == ACTION_PLAY) {//播放
				if (ContansUtils.getLastMusic() == null && ContansUtils.list.size() > 0) {
					playRandom();
				} else {
					play(ContansUtils.getLastMusic());
				}

			} else if (intent.getAction() == ACTION_PRE) {//上一首
				playPre();

			} else if (intent.getAction() == ACTION_NEXT) {//下一首
				playNext();

			} else if (intent.getAction() == ACTION_PAUSE) {//暂停
				if (media != null && media.isPlaying()) {
					media.pause();
				}
				NotificationUtils.updateNotification(getApplicationContext());

			} else if (intent.getAction() == ACTION_SETTIME) {//设置睡眠时间
				Timer timer = new Timer();
				if (ContansUtils.setTime) {
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							Log.i(TAG, "开启睡眠时间,还剩:" 
									+ ToolUtils.getTimeFormat(ContansUtils.lastTime * 1000));
							Intent setTime = new Intent();
							setTime.setAction(ACTION_SETTIME);
							sendBroadcast(setTime);

							if (ContansUtils.lastTime == 5) {//最后五秒显示退出按钮
								Intent toExitIntent = new Intent();
								toExitIntent.setAction(ACTION_TOEXIT);
								sendBroadcast(toExitIntent);
								
							} else if (ContansUtils.lastTime == 0) {//无任何操作则默认退出APP
								Intent exitIntent = new Intent();
								exitIntent.setAction(ACTION_EXIT);
								sendBroadcast(exitIntent);
							}
						}
					}, 1000);

				} else ToolUtils.setTimerNull(timer);
			} 
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/** 
	 * @Title: isPlaying 
	 * @Description: TODO 获取当前播放状态
	 * @return  
	 * @return boolean
	 * @author wuhuihui  
	 * @date 2016年6月2日 上午11:13:01
	 */
	public static boolean isPlaying() {
		if (media != null && media.isPlaying()) {
			return true;
		} 
		return false;
	}

	/** 
	 * @Title: playNext 
	 * @Description: TODO  播放下一首（取决于顺序播放还是随机播放）
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月10日 下午2:41:30
	 */
	private void playNext() {
		if (ContansUtils.getLastMusic() == null) {
			play(ContansUtils.list.get(0));//如果无最后播放记录，则播放第一首

		} else {
			if (ContansUtils.getPlayMode() == Music.mode_circle 
					|| ContansUtils.getPlayMode() == Music.mode_single ) {
				if (ContansUtils.getLastMusic().getId() == ContansUtils.list.size()) {//循环播放
					play(ContansUtils.list.get(0));//如果播放到列表的最后一首，则从头开始播放

				} else { //否则播放列表的下一首音乐
					play(ContansUtils.list.get(ContansUtils.getLastMusic().getId()));

				}

			} else if (ContansUtils.getPlayMode() == Music.mode_order) {//列表播放
				if (ContansUtils.getLastMusic().getId() == ContansUtils.list.size()) {
					//如果播放到列表的最后一首，则停止播放,释放播放器
					media.release();

				} else { //否则播放列表的下一首音乐
					play(ContansUtils.list.get(ContansUtils.getLastMusic().getId()));

				}

			} else if (ContansUtils.getPlayMode() == Music.mode_random) {//随机播放
				playRandom();

			}
		}
	}
	/** 
	 * @Title: playPre 
	 * @Description: TODO   播放上一首（取决于顺序播放还是随机播放）
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月11日 下午5:00:31
	 */
	private void playPre() {
		if (ContansUtils.getLastMusic() == null) {
			play(ContansUtils.list.get(0));//如果无最后播放记录，则播放第一首

		} else {
			if (ContansUtils.getPlayMode() == Music.mode_circle || ContansUtils.getPlayMode() == Music.mode_single ) {
				if (ContansUtils.getLastMusic().getId() == 1) {
					play(ContansUtils.list.get(ContansUtils.list.size() - 1));//如果播放到列表的第一首，则从尾首开始播放
				} else { //否则播放列表的上一首音乐
					play(ContansUtils.list.get(ContansUtils.getLastMusic().getId() - 2));
				}

			} else if (ContansUtils.getPlayMode() == Music.mode_order) {//列表播放
				if (ContansUtils.getLastMusic().getId() == 1) {
					//如果播放到列表的第一首,则停止播放,释放播放器
					media.release();
				} else { //否则播放列表的上一首音乐
					play(ContansUtils.list.get(ContansUtils.getLastMusic().getId() - 1));
				}

			} else if (ContansUtils.getPlayMode() == Music.mode_random) {//随机播放
				playRandom();

			}

		}
	}

	/** 
	 * @Title: getRandom 获取随机音乐并播放
	 * @Description: TODO   
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午3:05:09
	 */
	private void playRandom() {
		int location = (int)(Math.random() * (ContansUtils.list.size()));
		Music music = ContansUtils.list.get(location);
		if (music != null) {
			Log.i(TAG + "---" + "随便来一首！", location + "---" +  music.toString());
			play(ContansUtils.list.get(location));
		}
	}

	/** 
	 * @Title: play 
	 * @Description: TODO 播放音乐
	 * @param music  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午3:03:45
	 */
	private void play(Music music) {
		if (music != null) {
			try {
				if (media != null) {//释放播放器并置空
					media.release();
					media = null;
				}

				//创建播放器，设置并播放音乐
				Log.i(TAG, "创建播放器，设置并播放音乐");
				media = new MediaPlayer();
				media.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
				media.setOnCompletionListener(new MediaCompletionListener());
				media.setOnErrorListener(new MediaErrorListener());

				media.setDataSource(music.getUrl());
				media.prepare();
				media.start();

				media.seekTo(music.getProgress());

				Log.i(TAG, "保存播放位置...." + music.getProgress());
				ContansUtils.setLastMusic(getApplicationContext(), music, music.getProgress());

				Intent playIntent = new Intent();
				playIntent.setAction(ACTION_PLAY);
				sendBroadcast(playIntent);

				NotificationUtils.updateNotification(getApplicationContext());

			} catch (Exception e) {}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * @Title: MediaErrorListener  
	 * @Description: TODO 播放时候错误回调 
	 * @author wuhuihui
	 * @date 2016年5月6日 下午5:09:50 
	 */
	private class MediaErrorListener implements OnErrorListener {
		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			Log.i(TAG, "播放时候遇到错误，播放下一首");
			playNext();
			return false;
		}
	}

	/**
	 * @Title: MediaCompletionListener   
	 * @Description: TODO 播放完成事件
	 * @author wuhuihui
	 * @date 2016年5月6日 下午5:10:28 
	 */
	private class MediaCompletionListener implements OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			if (ContansUtils.getPlayMode() == Music.mode_single) {
				ContansUtils.setLastMusicProgress(getApplicationContext(), 0);
				play(ContansUtils.getLastMusic());

			} else {
				playNext();

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		media.stop();
		media.release();
		media = null;
		
		if (notifyReceiver != null)  unregisterReceiver(notifyReceiver);
		if (screenReceiver != null)  unregisterReceiver(screenReceiver);
	}

}
