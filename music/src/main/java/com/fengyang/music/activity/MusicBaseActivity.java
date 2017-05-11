package com.fengyang.music.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

/**
 * @Title: MusicBaseActivity
 * @Description: TODO
 * 所有Activity的基类，用来监听睡眠时间，显示倒计时，弹出退出app对话框,
 * 右滑手势退出activity,设置皮肤等操作
 * @author wuhuihui
 * @date 2016年6月3日 下午2:07:20 
 */
public class MusicBaseActivity extends BaseActivity {

	protected Receiver mReceiver;//APP退出接收器

	//退出对话框
	protected AlertDialog exitDialog = null;
	protected AlertDialog.Builder exitBuilder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mReceiver = new Receiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(PlayService.ACTION_SETTIME);
		myFilter.addAction(PlayService.ACTION_TOEXIT);
		myFilter.addAction(PlayService.ACTION_EXIT);
		registerReceiver(mReceiver, myFilter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if ((! TAG.contains("SplashActivity"))
				&& (! TAG.contains("LockActivity")
				&& (! TAG.contains("PlayActivity")))) {
			setTimeInfo();
		}

		//设置皮肤
		if (MusicUtils.getSkin() == 0) {
			getWindow().setBackgroundDrawableResource(android.R.color.white);
		} else {
			getWindow().setBackgroundDrawableResource(MusicUtils.getDrawableSkin());
		}

		if (MusicUtils.isNight) {
			getWindow().setBackgroundDrawableResource(android.R.color.black);
		}

	}

	/**
	 * @Title: setTimeInfo
	 * @Description: TODO  显示当前睡眠时间倒计时
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月31日 上午11:52:30
	 */
	private void setTimeInfo() {
		LogUtils.i(TAG, "setTimeInfo");
		if (MusicUtils.setTime && MusicUtils.lastTime != 0 && (! TAG.contains("PlayActivity"))) {

			setRightBtnListener("倒计时\n" + StringUtils.getTimeFormat(MusicUtils.lastTime * 1000), new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(), SetTimeActivity.class));
				}
			});
		}

	}

	/**
	 * @Title: Receiver
	 * @Description: TODO 自定义一个广播接收器
	 * @author wuhuihui
	 * @date 2016年5月10日 下午2:20:31
	 */
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//处理接收到的内容
			if(intent != null){
				LogUtils.i(TAG, "OnReceiver---" + intent.getAction());
				if (intent.getAction() == PlayService.ACTION_SETTIME) {
					setTimeInfo();

				} else if (intent.getAction() == PlayService.ACTION_TOEXIT) {
					showExitDialog();

				} else if (intent.getAction() == PlayService.ACTION_EXIT) {
					//无任何操作则默认退出APP
					if (MusicUtils.setTime) {
						if (exitDialog != null && exitDialog.isShowing()) 	exitDialog.dismiss();
						MusicUtils.exitAPP(getApplicationContext());
					}
				}
			}
		}
	}


	/**
	 * @Title: showExitDialog
	 * @Description: TODO  设定睡眠时间结束，确认是否退出app
	 * @return void
	 * @author wuhuihui
	 * @date 2016年5月20日 下午3:04:17
	 */
	private void showExitDialog() {

		exitBuilder = new AlertDialog.Builder(MusicBaseActivity.this);
		exitBuilder.setMessage("APP退出,是否确定?");
		exitBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MusicUtils.exitAPP(getApplicationContext());

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MusicUtils.setLastTime(context, 0);
				MusicUtils.setPlaytime(context, 0);
				MusicUtils.setTime = false;
			}
		});

		exitDialog = exitBuilder.create();
		exitDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

}
