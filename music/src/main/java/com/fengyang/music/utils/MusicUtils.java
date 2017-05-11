package com.fengyang.music.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.fengyang.music.R;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.toollib.utils.ActivityUtils;
import com.fengyang.toollib.utils.ContansUtils;
import com.fengyang.toollib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Title: MusicUtils
 * @Description: TODO 音乐工具类
 * @author wuhuihui
 * @date 2016年5月10日 下午4:48:08 
 */
public class MusicUtils {

	private static String TAG = "MusicUtils";

	public static List<Music> list = new ArrayList<Music>();//音乐列表

	public static boolean isNight = false;//夜间模式

	public static Timer timer = null;//睡眠时间倒计时
	public static int playTime = 0;//设置的睡眠时间
	public static int lastTime = 0;//设置剩余睡眠时间
	public static boolean setTime = false;//睡眠时间的设置开关
	public static boolean isUserDefined = false;//自定义睡眠时间开关

	/**
	 * @Title: startService
	 * @Description: TODO 向PlayService传输播放/暂停/上一首/下一首指令
	 * @param context
	 * @param action
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月17日 下午2:11:38
	 */
	public static void startService(Context context, String action){
		//发送通知
		NotificationUtils.updateNotification(context);
		Intent intent = new Intent(context, PlayService.class);
		intent.setAction(action);
		context.startService(intent);
	}

	/** 
	 * @Title: getMusicList 
	 * @Description: TODO 获取音乐文件
	 * @param context  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月27日 下午2:56:24
	 */
	public static void getMusicList(Context context) {
		int id = 1;
		Cursor cursor = context.getContentResolver().query( 
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, 
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER); 
		for (cursor.moveToFirst();! cursor.isAfterLast();cursor.moveToNext()) {
			list.add(new Music(id, //音乐编号
					cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)), //音乐标题
					cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)), //音乐的专辑名
					cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)), //音乐的歌手名
					cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)), //音乐文件的路径 
					cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)))); //音乐的总播放时长
			id ++;
		}
		LogUtils.i(TAG, list.toString());
		cursor.close();
	}

	/**
	 * @Title: setLastMusic 
	 * @Description: TODO 记录最后播放的音乐和播放进度
	 * @param music 音乐
	 * @param progress  进度
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:24:11
	 */
	public static void setLastMusic(Music music, int progress) {
		if(music != null){
			ContansUtils.put("id", music.getId());
			ContansUtils.put("title", music.getTitle());
			ContansUtils.put("album", music.getAlbum());
			ContansUtils.put("artist", music.getArtist());
			ContansUtils.put("url", music.getUrl());
			ContansUtils.put("duration", music.getDuration());
			ContansUtils.put("progress", progress);
			LogUtils.i(TAG, "setLastMusic--" + music.toString());
		}

		getLastMusic();
	}

	/** 
	 * @Title: setLastMusicProgress 
	 * @Description: TODO 记录最后一首音乐的播放进度
	 * @param progress
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月10日 下午4:18:07
	 */
	public static void setLastMusicProgress(int progress) {
		if(getLastMusic() != null){
			ContansUtils.put("progress", progress);
			LogUtils.i(TAG, getLastMusic().toString());
		}
	}

	/** 
	 * @Title: getLastMusic 
	 * @Description: TODO 获取最后播放的音乐及播放的进度
	 * @return  
	 * @return Music
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:24:58
	 */
	public static Music getLastMusic() {
		if ((int) ContansUtils.get("id", 0) != 0) {
			Music music = new Music((int) ContansUtils.get("id", 0),
					(String) ContansUtils.get("title", ""),
					(String) ContansUtils.get("album", ""),
					(String) ContansUtils.get("artist", ""),
					(String) ContansUtils.get("url", ""),
					(int) ContansUtils.get("duration", 0),
					(int) ContansUtils.get("progress", 0));
			return music;
		} else {
			return null;
		}
	}

	/** 
	 * @Title: setPlayMode 
	 * @Description: TODO 设置播放模式
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:46:48
	 */
	public static void setPlayMode (int mode) {
		ContansUtils.put("mode", mode);
	}

	/** 
	 * @Title: getsetPlayMode 
	 * @Description: TODO 获取播放模式
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:47:12
	 */
	public static int getPlayMode () {
		return (int) ContansUtils.get("mode", 0);
	}

	/** 
	 * @Title: setPlaytime 
	 * @Description: TODO 设置睡眠时间(多长时间后退出app，单位秒)
	 * @param time 分
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月18日 下午3:37:30
	 */
	public static void setPlaytime (Context context, final int time) {
		LogUtils.i(TAG, "写入设定睡眠时间---" + time * 60);

		playTime = time * 60;
		if (time > 0) {
			setTime = true;
			setLastTime(context, time * 60);
		} else {
			setTime = false;
		}
	}

	/** 
	 * @Title: setLastTime 
	 * @Description: TODO 写入剩余睡眠时间
	 * @param time  秒
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月19日 下午4:32:14
	 */
	public static void setLastTime (final Context context, int time) {
		LogUtils.i(TAG, "写入剩余睡眠时间---" + time);
		MusicUtils.setTimerNull(timer);
		lastTime = time;
		if (setTime) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					LogUtils.i(TAG, "timer is scheduling...");
					int getTime = lastTime;
					if (getTime > 0) {
						MusicUtils.startService(context, PlayService.ACTION_SETTIME);
						setLastTime(context, -- getTime);
					}
				}
			}, 1000);

		} else {
			if (isUserDefined)  isUserDefined = false;
			ContansUtils.put("time", 0);
			ContansUtils.put("lastTime", 0);
			LogUtils.i(TAG, "关闭睡眠时间\n写入设定睡眠时间---" + playTime
					+ ",写入剩余睡眠时间---" + lastTime);
		}
	}

	/** 
	 * @Title: getTimeIndex 返回时间标识
	 * @Description: TODO 
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月19日 下午3:53:13
	 */
	public static int getTimeIndex() {
		int time = playTime / 60;//秒转为分
		LogUtils.i(TAG, "playTime" + playTime);
		if (time == 0) return 0;
		if(isUserDefined) return 6;
		if (time == 10) return 1;
		if (time == 20) return 2;
		if (time == 30) return 3;
		if (time == 60) return 4;
		if (time == 90) return 5;
		return 0;
	}

	/** 
	 * @Title: setSkin 
	 * @Description: TODO 设置皮肤参数
	 * @param skin
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月20日 下午4:11:56
	 */
	public static void setSkin(int skin) {
		ContansUtils.put("skin", skin);
	}

	/** 
	 * @Title: getSkin 
	 * @Description: TODO 获取皮肤
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月20日 下午4:12:14
	 */
	public static int getSkin() {
		return (int) ContansUtils.get("skin", 0);
	}

	/** 
	 * @Title: setHistory 
	 * @Description: TODO 写入搜索历史记录
	 * @param history
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 上午10:16:53
	 */
	public static void addHistory(String history) {
		if (! TextUtils.isEmpty(history)) {
			String oldHistory = (String) ContansUtils.get("history", "");
			ContansUtils.put("history", oldHistory + history + ",");
		}
	}

	/** 
	 * @Title: getHistory 
	 * @Description: TODO 获取历史记录,去重
	 * @return  
	 * @return String
	 * @author wuhuihui  
	 * @date 2016年6月6日 上午10:17:08
	 */
	public static List<String> getHistory() {
		List<String> hisList = new ArrayList<String>();
		String history = (String) ContansUtils.get("history", "");
		for (int i = 0; i < history.split(",").length; i++) {
			if (! TextUtils.isEmpty(history.split(",")[i])) {//截取时去空字符
				hisList.add(history.split(",")[i]);
			}
		}
		removeRepeat(hisList);//去重
		LogUtils.i(TAG, "搜索历史记录---" + hisList.size() + ":" + hisList.toString());
		return hisList;

	}

	/** 
	 * @Title: clearHistory 
	 * @Description: TODO 清除搜索记录
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 下午5:38:12
	 */
	public static void clearHistory() {
		ContansUtils.put("history", "");
	}

	/** 
	 * @Title: setViewSkin 
	 * @Description: TODO 获取皮肤所对应的R值
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月20日 下午5:14:02
	 */
	public static int getDrawableSkin() {
		int draw = MusicUtils.getSkin();
		LogUtils.i(TAG, "getSkin---" + draw);
		if (draw == 0)	return R.drawable.transparent;
		if (draw == 1)	return R.drawable.skin1;
		if (draw == 2)	return R.drawable.skin2;
		if (draw == 3)	return R.drawable.skin3;
		if (draw == 4)	return R.drawable.skin4;
		if (draw == 5)	return R.drawable.skin5;
		if (draw == 6)	return R.drawable.skin6;
		if (draw == 7)	return R.drawable.skin7;
		if (draw == 8)	return R.drawable.skin8;
		if (draw == 9)	return R.drawable.skin9;
		return draw;
	}
	
	/** 
	 * @Title: setIsLock 
	 * @Description: TODO 设置锁屏
	 * @param flag
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月26日 下午5:50:25
	 */
	public static void setIsLock (Boolean flag) {
		ContansUtils.put("lock", flag);
	}
	
	/** 
	 * @Title: getIsLock 
	 * @Description: TODO 获取是否锁屏,默认为true
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月18日 下午3:39:07
	 */
	public static boolean getIsLock () {
		return (boolean) ContansUtils.get("lock", true);
	}
	
	/** 
	 * @Title: setIsShaker 
	 * @Description: TODO 设置摇一摇
	 * @param flag
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月26日 下午5:50:25
	 */
	public static void setIsShaker (Boolean flag) {
		ContansUtils.put("shaker", flag);
	}

	/** 
	 * @Title: getIsShaked 
	 * @Description: TODO 获取是否摇一摇
	 * @return  
	 * @return int
	 * @author wuhuihui  
	 * @date 2016年5月18日 下午3:39:07
	 */
	public static boolean getIsShaked () {
		return (boolean) ContansUtils.get("shaker", false);
	}

	/** 
	 * @Title: clearCache 
	 * @Description: TODO  清除缓存
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月27日 下午3:51:27
	 */
	public static void clearCache () {
		ContansUtils.clear();
	}

	/** 
	 * @Title: removeRepeat 
	 * @Description: TODO 去重
	 * @param list
	 * @return  
	 * @return List<String>
	 * @author wuhuihui  
	 * @date 2016年6月6日 下午3:56:43
	 */
	public static List<String> removeRepeat(List<String> list) {
		for ( int i = 0 ; i < list.size() - 1 ; i ++ ) { 
			for ( int j  = list.size()  - 1 ; j > i; j -- ) { 
				if (list.get(j).equals(list.get(i))) { 
					list.remove(j); 
				} 
			} 
		}
		LogUtils.i(TAG, list.toString());
		return list;

	}

	/**
	 * @Title: setTimerNull
	 * @Description: TODO 将计时器置空
	 * @param timer
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月19日 下午5:29:55
	 */
	public static void setTimerNull(Timer timer){
		if (timer != null) {
			LogUtils.i(TAG, "释放timer");
			timer.purge();
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * @Title: exitAPP
	 * @Description: TODO 退出APP
	 * @param context
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月30日 下午2:27:20
	 */
	public static void exitAPP(Context context){
		LogUtils.i(TAG, "退出APP");
		setLastTime(context, 0);
		NotificationUtils.cancel();
		context.stopService(new Intent(context, PlayService.class));
		ActivityUtils.AppExit(context);
	}

}
