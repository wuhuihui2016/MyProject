package com.fengyang.myproject.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.fengyang.music.utils.MusicUtils;
import com.fengyang.myproject.R;

import java.util.Timer;
import java.util.TimerTask;

/**
* @Title: SplashActivity   
* @Description: TODO 欢迎界面
* @author wuhuihui
* @date 2016年6月3日 下午2:41:41 
*/
public class SplashActivity extends Activity {
	
	private String TAG = "SplashActivity";

	private Button time;
	private Timer timer;
	private int i = 5;//5秒倒计时进入主界面

	@SuppressLint("InlinedApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//沉浸式状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.activity_splash);

		time = (Button) findViewById(R.id.time);

		timer = new Timer();
		timer.schedule(new MyTask(), 0, 1000);
		
		time.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toNext();
			}
		});

	}
	
	/** 
	* @Title: toNext 
	* @Description: TODO  跳转主界面 
	* @return void
	* @author wuhuihui  
	* @date 2016年6月3日 下午2:42:30
	*/
	private void toNext() {
		MusicUtils.setTimerNull(timer);
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (i == 0) {
				toNext();
			} else {
				time.setText(i + "秒");
			}
		};
	};

	/**
	 * @Title: MyTask   
	 * @Description: TODO 刷新读秒
	 * @author wuhuihui
	 * @date 2016年5月18日 上午11:21:56 
	 */
	private class MyTask extends TimerTask {

		@Override
		public void run() {
			Log.i(TAG, "还剩"+ (i --) + "秒");
			handler.sendMessage(new Message());
		}
	}
	
	/** 
	* @Title: OnClick 
	* @Description: TODO 点击事件
	* @param v  
	* @return void
	* @author wuhuihui  
	* @date 2016年6月12日 下午5:24:09
	*/
	public void OnClick(View v) {
		toNext();
	}

}
