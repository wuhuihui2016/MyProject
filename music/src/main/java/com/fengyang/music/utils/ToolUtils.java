package com.fengyang.music.utils;

import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.fengyang.music.service.PlayService;

public class ToolUtils {
	
	private static String TAG = "ToolUtils";
	
	/** 
	 * @Title: getTimeFormat 
	 * @Description: TODO 将毫秒转为00:00:00/00:00秒格式(适用于计算睡眠时间)
	 * @param time 毫秒
	 * @return  
	 * @return String
	 * @author wuhuihui  
	 * @date 2016年5月12日 上午11:13:54
	 */
	public static String getTimeFormat(int time) {
		  String timeStr = "";
	        int s = time/1000;   //秒
	        int h = s / 3600;    //求整数部分 ，小时
	        int r = s % 3600;    //求余数
	        int m = 0;
	        if(r > 0) {
	            m = r / 60;    //分
	            r = r % 60;    //求分后的余数，即为秒
	        }
	        if(h < 10) {
	            timeStr = "0" + h + ":";
	            if (h == 0) {
					timeStr = "";
				}
	        } else {
	            timeStr = h + ":";
	        }
	         
	        if(m < 10) {
	            timeStr = timeStr + "0" + m + ":";
	        } else {
	            timeStr = timeStr + m + ":";
	        }
	         
	        if(r < 10) {
	            timeStr = timeStr + "0" + r;
	        } else {
	            timeStr = timeStr + r;
	        }
	        return timeStr;
	}

	/** 
	* @Title: hideInput 
	* @Description: TODO 隐藏键盘
	* @param activity  
	* @return void
	* @author wuhuihui  
	* @date 2016年5月17日 下午2:15:47
	*/
	public static void hideInput(Activity activity) {
		((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
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
		Intent intent = new Intent(context, PlayService.class);
		intent.setAction(action);
		context.startService(intent);
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
			Log.i(TAG, "释放timer");
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
		Log.i(TAG, "退出APP");
		ContansUtils.setLastTime(context, 0);
		NotificationUtils.cancel();
		context.stopService(new Intent(context, PlayService.class));
		ActivityUtils.AppExit(context);
	}
	
	
	
}
