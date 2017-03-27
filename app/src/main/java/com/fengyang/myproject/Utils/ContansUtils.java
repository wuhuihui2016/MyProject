package com.fengyang.myproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Title: ContansUtils
 * @Description: TODO 常量类
 * @author wuhuihui
 * @date 2015年11月20日 下午4:24:40 
 */
public class ContansUtils {

	private static String TAG = "ContansUtils";

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * @param context 上下文
	 * @param key 键
	 * @param object  要保存的值
	 */
	public static void put(Context context, String key, Object object) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
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
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 *
	 * @param context 上下文
	 * @param key 键
	 * @param defaultObject 默认值
	 * @return Object 根据默认值，来确定返回值的具体类型
	 */
	public static Object get(Context context, String key, Object defaultObject) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
	 *
	 * @param context  上下文
	 * @param fileName  保存在手机里面的文件名
	 * @param key  键
	 */
	public static void remove(Context context, String fileName, String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 *
	 * @param context  上下文
	 * @param fileName  保存在手机里面的文件名
	 */
	public static void clear(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 *
	 * @param context  上下文
	 * @param fileName 保存在手机里面的文件名
	 * @param key  键
	 * @return 成功或失败
	 */
	public static boolean contains(Context context, String fileName, String key) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 *
	 * @param context  上下文
	 * @param fileName  保存在手机里面的文件名
	 * @return
	 */
	public static Map<String, ?> getAll(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}
			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * @param editor
		 */
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}

}
