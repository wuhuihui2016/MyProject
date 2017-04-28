package com.fengyang.music.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.fengyang.music.R;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;

/**
 * @Title: ContansUtils   
 * @Description: TODO 常量类
 * @author wuhuihui
 * @date 2016年5月10日 下午4:48:08 
 */
public class ContansUtils {

	private static String TAG = "ContansUtils";
	private static SharedPreferences preference = null;
	private static Editor editor = null;

	public static List<Music> list = new ArrayList<Music>();//音乐列表

	public static boolean isNight = false;//夜间模式

	public static Timer timer = null;//睡眠时间倒计时
	public static int playTime = 0;//设置的睡眠时间
	public static int lastTime = 0;//设置剩余睡眠时间
	public static boolean setTime = false;//睡眠时间的设置开关
	public static boolean isUserDefined = false;//自定义睡眠时间开关

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
		Log.i(TAG, list.toString());
		cursor.close();
	}

	/** 
	 * @Title: getPreference 
	 * @Description: TODO 
	 * @param context
	 * @return  
	 * @return SharedPreferences
	 * @author wuhuihui  
	 * @date 2016年5月10日 下午4:43:05
	 */
	public static SharedPreferences getPreference(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/** 
	 * @Title:  getEditor
	 * @Description: TODO 获取SharedPreferences.Editor
	 * @param context
	 * @return  
	 * @return SharedPreferences
	 * @author wuhuihui  
	 * @date 2016年5月10日 下午4:35:49
	 */
	public static void getEditor(Context context) {
		preference = getPreference(context);
		editor = preference.edit();
	}

	/** 
	 * @Title: setLastMusic 
	 * @Description: TODO 记录最后播放的音乐和播放进度
	 * @param context
	 * @param music 音乐
	 * @param progress  进度
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:24:11
	 */
	public static void setLastMusic(Context context, Music music, int progress) {
		if(music != null){
			if (editor == null) {
				getEditor(context);
			}

			editor.putInt("id", music.getId());
			editor.putString("title", music.getTitle());
			editor.putString("album", music.getAlbum());
			editor.putString("artist", music.getArtist());
			editor.putString("url", music.getUrl());
			editor.putInt("duration", music.getDuration());
			editor.putInt("progress", progress);
			editor.commit();
			Log.i(TAG, music.toString());
		}
	}

	/** 
	 * @Title: setLastMusicProgress 
	 * @Description: TODO 记录最后一首音乐的播放进度
	 * @param context
	 * @param progress  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月10日 下午4:18:07
	 */
	public static void setLastMusicProgress(Context context, int progress) {
		if (editor == null) {
			getEditor(context);
		}
		if(getLastMusic() != null){
			editor.putInt("progress", progress);
			editor.commit();
			Log.i(TAG, getLastMusic().toString());
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
		if (preference != null && 
				(! TextUtils.isEmpty(preference.getString("title", null)))) {
			return new Music(preference.getInt("id", 0), 
					preference.getString("title", null), 
					preference.getString("album", null), 
					preference.getString("artist", null), 
					preference.getString("url", null), 
					preference.getInt("duration", 0),
					preference.getInt("progress", 0));
		} else {
			return null;
		}
	}

	/** 
	 * @Title: setPlayMode 
	 * @Description: TODO 设置播放模式
	 * @param tag  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:46:48
	 */
	public static void setPlayMode (Context context, int mode) {
		if (editor == null) {
			getEditor(context);
		}
		editor.putInt("mode", mode);
		editor.commit();
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
		return preference.getInt("mode", 0);
	}

	/** 
	 * @Title: setPlaytime 
	 * @Description: TODO 设置睡眠时间(多长时间后退出app，单位秒)
	 * @param context
	 * @param time 分 
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月18日 下午3:37:30
	 */
	public static void setPlaytime (final Context context, final int time) {
		Log.i(TAG, "写入设定睡眠时间---" + time * 60);

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
	 * @param context
	 * @param time  秒
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月19日 下午4:32:14
	 */
	public static void setLastTime (final Context context, int time) {
		Log.i(TAG, "写入剩余睡眠时间---" + time);
		ToolUtils.setTimerNull(timer);
		lastTime = time;
		if (setTime) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.i(TAG, "timer is scheduling...");
					int getTime = lastTime;
					if (getTime > 0) {
						ToolUtils.startService(context, PlayService.ACTION_SETTIME);
						setLastTime(context, -- getTime);
					}
				}
			}, 1000);

		} else {
			if (isUserDefined)  isUserDefined = false;
			editor.putInt("time", 0);
			editor.putInt("lastTime", 0);
			editor.commit();
			Log.i(TAG, "关闭睡眠时间\n写入设定睡眠时间---" + playTime 
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
		Log.i(TAG, "playTime" + playTime);
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
	 * @param context
	 * @param skin  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月20日 下午4:11:56
	 */
	public static void setSkin(final Context context, int skin) {
		if (editor == null) {
			getEditor(context);
		}
		editor.putInt("skin", skin);
		editor.commit();
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
		return preference.getInt("skin", 0);
	}

	/** 
	 * @Title: setHistory 
	 * @Description: TODO 写入搜索历史记录
	 * @param context
	 * @param history  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 上午10:16:53
	 */
	public static void addHistory(final Context context, String history) {
		if (editor == null) {
			getEditor(context);
		}
		if (! TextUtils.isEmpty(history)) {
			String oldHistory =  preference.getString("history", "");
			editor.putString("history", oldHistory + history + ",");
			editor.commit();
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
		String history = preference.getString("history", "");
		for (int i = 0; i < history.split(",").length; i++) {
			if (! TextUtils.isEmpty(history.split(",")[i])) {//截取时去空字符
				hisList.add(history.split(",")[i]);
			}
		}
		removeRepeat(hisList);//去重
		Log.i(TAG, "搜索历史记录---" + hisList.size() + ":" + hisList.toString());
		return hisList;

	}

	/** 
	 * @Title: clearHistory 
	 * @Description: TODO 清除搜索记录
	 * @param context  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 下午5:38:12
	 */
	public static void clearHistory(final Context context) {
		if (editor == null) {
			getEditor(context);
		}
		editor.putString("history", "");
		editor.commit();
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
		int draw = ContansUtils.getSkin();
		Log.i(TAG, "getSkin---" + draw);
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
	 * @param context
	 * @param flag  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月26日 下午5:50:25
	 */
	public static void setIsLock (Context context, Boolean flag) {
		if (editor == null) {
			getEditor(context);
		}
		editor.putBoolean("lock", flag);
		editor.commit();
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
		return preference.getBoolean("lock", true);
	}
	
	/** 
	 * @Title: setIsShaker 
	 * @Description: TODO 设置摇一摇
	 * @param context
	 * @param flag  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月26日 下午5:50:25
	 */
	public static void setIsShaker (Context context, Boolean flag) {
		if (editor == null) {
			getEditor(context);
		}
		editor.putBoolean("shaker", flag);
		editor.commit();
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
		return preference.getBoolean("shaker", false);
	}

	/** 
	 * @Title: clearCache 
	 * @Description: TODO  清除缓存
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月27日 下午3:51:27
	 */
	public static void clearCache (Context context) {
		if (editor == null) {
			getEditor(context);
		}
		editor.clear();
		editor.commit();
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
		Log.i(TAG, list.toString());
		return list;

	}

}
