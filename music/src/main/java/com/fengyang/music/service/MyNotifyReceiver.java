package com.fengyang.music.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fengyang.music.utils.MusicUtils;
import com.fengyang.music.utils.NotificationUtils;
import com.fengyang.toollib.utils.LogUtils;

/**
* @Title: MyNotifyReceiver   
* @Description: TODO 通知接收处理
* @author wuhuihui
* @date 2016年5月17日 下午2:44:24 
*/
public class MyNotifyReceiver extends BroadcastReceiver {
	
	private static final String TAG = "MyNotifyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		//处理接收到的内容
		if(intent != null){
			LogUtils.i(TAG, "OnReceiver---" + intent.getAction());
			if (intent.getAction().equals(NotificationUtils.ACTION_PLAY)) {
				MusicUtils.startService(context, PlayService.ACTION_PLAY);
				
			} else if (intent.getAction().equals(NotificationUtils.ACTION_PAUSE)) {
				MusicUtils.startService(context, PlayService.ACTION_PAUSE);
				
			} else if (intent.getAction().equals(NotificationUtils.ACTION_NEXT)) {
				MusicUtils.startService(context, PlayService.ACTION_NEXT);
				
			} else if (intent.getAction().equals(NotificationUtils.ACTION_CLOSE)) {
				NotificationUtils.cancel();
				
			}
		}
	}
}
