package com.fengyang.myproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengyang.myproject.R;

/**
* @Title: SplashActivity   
* @Description: TODO 欢迎界面
* @author wuhuihui
* @date 2016年6月3日 下午2:41:41 
*/
public class SplashActivity extends Activity {
	
	private String TAG = "SplashActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//沉浸式状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.activity_splash);

		final TextView time = (TextView) findViewById(R.id.time);
		new CountDownTimer(5000, 1000) {//5秒后自动跳转
			public void onTick(long millisUntilFinished) {
				time.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						toNext();
					}
				});
				time.setText(millisUntilFinished / 1000 + "秒");
			}

			public void onFinish() {
				toNext();
			}
		}.start();

	}
	
	/** 
	* @Title: toNext 
	* @Description: TODO  跳转主界面 
	* @return void
	* @author wuhuihui  
	* @date 2016年6月3日 下午2:42:30
	*/
	private void toNext() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

}
