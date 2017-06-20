package com.fengyang.toollib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.fengyang.toollib.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Map;

/**
 * @Title: ContansUtils
 * @Description: TODO 常量类
 * @author wuhuihui
 * @date 2015年11月20日 下午4:24:40 
 */
public class ContansUtils {

	private static final String TAG = "ContansUtils";
	private static SharedPreferences preferences;
	private static SharedPreferences.Editor editor;

	/**
	 * 设置存储空间，获取编辑器
	 * @param context
	 * @return
     */
	public static void setPres(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = preferences.edit();
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * @param key 键
	 * @param object  要保存的值
	 */
	public static void put(String key, Object object) {
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		editor.commit();
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * @param key 键
	 * @param defaultObject 默认值
	 * @return Object 根据默认值，来确定返回值的具体类型
	 */
	public static Object get(String key, Object defaultObject) {
		if (defaultObject instanceof String) {
			return preferences.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return preferences.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return preferences.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return preferences.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return preferences.getLong(key, (Long) defaultObject);
		}
		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 * @param key  键
	 */
	public static void remove(String key) {
		editor.remove(key).commit();
	}

	/**
	 * 清除默认SharedPreferences数据
	 */
	public static void clear() {
		editor.clear().commit();
	}

	/**
	 * 查询某个key是否已经存在
	 * @param key  键
	 * @return 成功或失败
	 */
	public static boolean contains(String key) {
		return preferences.contains(key);
	}

	/**
	 * 返回所有的键值对
	 * @return
	 */
	public static Map<String, ?> getAll() {
		return preferences.getAll();
	}

	// 显示图片的配置
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.mipmap.app_icon)
			.showImageForEmptyUri(R.mipmap.app_icon)
			.showImageOnFail(R.mipmap.app_icon)
			.resetViewBeforeLoading(false)
			.delayBeforeLoading(100)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.bitmapConfig(Bitmap.Config.ARGB_8888)
			.displayer(new FadeInBitmapDisplayer(50))// 图片加载好后渐入的动画时间
			.displayer(new RoundedBitmapDisplayer(150))//圆形图片显示，值越大越圆（<150就不圆）
			.build();

}
