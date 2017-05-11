package com.fengyang.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.fengyang.music.activity.LockActivity;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.utils.LogUtils;

/**
* @Title: ScreenReceiver 锁屏接收器
* @Description: TODO 
* @author wuhuihui
* @date 2016年5月26日 下午5:35:59 
*/
public class ScreenReceiver extends BroadcastReceiver {
	
	private static final String TAG = "ScreenReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		LogUtils.i(TAG, "OnReceiver---" + intent.getAction());
		
		if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
			if (PlayService.isPlaying() && MusicUtils.getIsLock()) {
				//仅当音乐播放和设置了锁屏时，锁屏界面才显示
				Intent lockedIntent = new Intent(context, LockActivity.class);
				lockedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
				context.startActivity(lockedIntent);
			}
			if (MusicUtils.getIsShaked()) {//仅当锁屏时摇一摇切歌才有效
				//定义sensor管理器  
				SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);  
				//震动  
				final Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
				//加速度传感器  
				mSensorManager.registerListener(new SensorEventListener() {
					
					@Override
					public void onSensorChanged(SensorEvent event) {
						// TODO Auto-generated method stub  
						int sensorType = event.sensor.getType();  
						
						//values[0]:X轴，values[1]：Y轴，values[2]：Z轴  
						float[] values = event.values;  
						
						if(sensorType == Sensor.TYPE_ACCELEROMETER){  
							
							/*因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机 
							 *的时候，瞬时加速度才会突然增大或减少。 
							 *所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置 
							 *就OK了~~~ 
							 */  
							if((Math.abs(values[0])>14 | Math.abs(values[1])>14 | Math.abs(values[2])>14)){  
								
								//摇动手机后，再伴随震动提示~~  
								vibrator.vibrate(100);
								LogUtils.i(TAG, "摇一摇切歌~~");
								//摇动手机后，切歌下一首
								MusicUtils.startService(context, PlayService.ACTION_NEXT);
							}  
						}  
					}
					
					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {}
				},  
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
				//还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，  
				//根据不同应用，需要的反应速率不同，具体根据实际情况设定  
				SensorManager.SENSOR_DELAY_NORMAL); 
			}
		
		}
	}

}
