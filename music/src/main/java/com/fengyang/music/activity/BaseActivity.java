package com.fengyang.music.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.ActivityUtils;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.NotificationUtils;
import com.fengyang.music.utils.ToolUtils;
import com.fengyang.music.view.ImageTextButtonView;

/**
 * @Title: BaseActivity   
 * @Description: TODO 
 * 所有Activity的基类，用来监听睡眠时间，显示倒计时，弹出退出app对话框,
 * 右滑手势退出activity,设置皮肤等操作
 * @author wuhuihui
 * @date 2016年6月3日 下午2:07:20 
 */
public class BaseActivity extends FragmentActivity implements OnTouchListener, OnGestureListener{

	protected String TAG;
	protected TextView tvTitle;
	protected Receiver mReceiver;//APP退出接收器

	//退出对话框
	protected AlertDialog exitDialog = null;
	protected AlertDialog.Builder exitBuilder = null;

	//闹钟组合控件
	protected ImageTextButtonView baseITButView;

	@SuppressWarnings("deprecation")//手势
	private GestureDetector detector = new GestureDetector(this);

	@SuppressLint("InlinedApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//沉浸式状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		ActivityUtils.addActivity(this);
		TAG = getLocalClassName().substring(getLocalClassName().lastIndexOf("/") + 1);

		//发送通知
		NotificationUtils.updateNotification(getApplicationContext());

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
		if (ContansUtils.getSkin() == 0) {
			getWindow().setBackgroundDrawableResource(android.R.color.white);
		} else {
			getWindow().setBackgroundDrawableResource(ContansUtils.getDrawableSkin());
		}

		if (ContansUtils.isNight) {
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
		Log.i(TAG, "setTimeInfo");
		baseITButView = (ImageTextButtonView) findViewById(R.id.baseITButView);

		if (ContansUtils.setTime && ContansUtils.lastTime != 0 && (! TAG.contains("PlayActivity"))) {
			baseITButView.setVisibility(View.VISIBLE);
			baseITButView.setTextViewText(
					ToolUtils.getTimeFormat(ContansUtils.lastTime * 1000));

			baseITButView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					startActivity(new Intent(getApplicationContext(), SetTimeActivity.class));
					return false;
				}
			});
		
		} else {
			baseITButView.setVisibility(View.GONE);
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
				Log.i(TAG, "OnReceiver---" + intent.getAction());
				if (intent.getAction() == PlayService.ACTION_SETTIME) {
					setTimeInfo();

				} else if (intent.getAction() == PlayService.ACTION_TOEXIT) {
					showExitDialog();

				} else if (intent.getAction() == PlayService.ACTION_EXIT) {
					//无任何操作则默认退出APP
					if (ContansUtils.setTime) {
						if (exitDialog != null && exitDialog.isShowing()) 	exitDialog.dismiss();
						ToolUtils.exitAPP(getApplicationContext());
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

		exitBuilder = new AlertDialog.Builder(BaseActivity.this);
		exitBuilder.setMessage("APP退出,是否确定?");
		exitBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ToolUtils.exitAPP(getApplicationContext());

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ContansUtils.setLastTime(getApplicationContext(), 0);
				ContansUtils.setPlaytime(getApplicationContext(), 0);
				ContansUtils.setTime = false;
			}
		});

		exitDialog = exitBuilder.create();
		exitDialog.show();
	}

	/**
	 * 手势滑动时别调用
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
		if (e1.getX() - e2.getX() > 110) { // 向左滑动
			Log.i(TAG, "向左滑动");
		} else if (e2.getX() - e1.getX() > 110) { // 向右滑动
			Log.i(TAG, "向右滑动");
			finish();
		}
		return false;
	}

	/**
	 * 长按时被调用
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		Log.d(TAG, "触发长按回调");
	}

	/**
	 * 滚动时调用
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	/**
	 * 在按下动作时被调用
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		Log.d(TAG, "按下回调");
		return false;
	}

	/**
	 * 按住时被调用
	 */
	@Override
	public void onShowPress(MotionEvent e) {
		Log.d(TAG, "按住不松回调");
	}

	/**
	 * 抬起时被调用
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.d(TAG, "触发抬起回调");
		return false;
	}

	/**
	 * 重写OnTouchListener的onTouch方法 此方法在触摸屏被触摸，即发生触摸事件（接触和抚摸两个事件）的时候被调用
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		detector.onTouchEvent(event);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

}
