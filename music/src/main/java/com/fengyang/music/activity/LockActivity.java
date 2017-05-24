package com.fengyang.music.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.service.MyNotifyReceiver;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.music.utils.NotificationUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Title: LockActivity   
 * @Description: TODO 锁屏界面
 * @author wuhuihui
 * @date 2016年5月12日 下午4:03:23 
 */
public class LockActivity extends MusicBaseActivity implements OnClickListener,
OnTouchListener, OnGestureListener {

	private TextView time, date, song;//时间/日期/音乐基本信息
	private ImageButton play, pause;//播放/暂停按钮
	private ImageView anim;//动画效果显示
	private RelativeLayout layout;//整布局
	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(this);
	private MyReceiver myReceiver;//内部接受器，控制界面指令
	private MyNotifyReceiver receiver;//通知的指令
	@SuppressLint("SimpleDateFormat") private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private Timer timer;

	@SuppressLint({ "InlinedApi"}) @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		setContentView(R.layout.activity_lock);
		
		//时间/日期/音乐基本信息
		time = (TextView) findViewById(R.id.time);
		date = (TextView) findViewById(R.id.date);
		song = (TextView) findViewById(R.id.song);
		
		time.setText(sdf.format(new Date()));
		date.setText(getTime());
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.sendMessage(new Message());
			}
		}, 0, 1000);

		//播放/暂停按钮
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);

		//启动动画(必须手动启动，否则无效果)
		anim = (ImageView) findViewById(R.id.anim);
		anim.setBackgroundResource(R.drawable.lock_anim);
		AnimationDrawable aDrawable = (AnimationDrawable) anim.getBackground();
		aDrawable.start();

		//获取整布局，设置手势监听事件
		layout = (RelativeLayout) findViewById(R.id.layout);
		layout.setOnTouchListener(this);

		//自定义播放接收器
		myReceiver = new MyReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(PlayService.ACTION_PLAY);
		myFilter.addAction(PlayService.ACTION_NEXT);
		myFilter.addAction(PlayService.ACTION_PAUSE);
		registerReceiver(myReceiver, myFilter);

		//注册通知接收器
		receiver = new MyNotifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NotificationUtils.ACTION_PLAY);
		filter.addAction(NotificationUtils.ACTION_NEXT);
		filter.addAction(NotificationUtils.ACTION_PAUSE);
		filter.addAction(NotificationUtils.ACTION_CLOSE);
		registerReceiver(receiver, filter);

		//设置当前播放状态并显示相关控件
		if (PlayService.media == null) { isPlaying(false); } 
		else { isPlaying(PlayService.media.isPlaying()); }

	}

	@SuppressLint("HandlerLeak")Handler handler = new Handler(){
		@SuppressLint("SimpleDateFormat") public void handleMessage(android.os.Message msg) {
			time.setText(sdf.format(new Date()));
			date.setText(getTime());
		};
	};

	@Override
	protected void onPause() {
		super.onPause();
		SystemUtils.stopTimer(timer);
	}

	/** 
	 * @Title: getTime 
	 * @Description: TODO 获取日期和星期
	 * @return  
	 * @return String
	 * @author wuhuihui  
	 * @date 2016年5月25日 下午4:05:30
	 */
	public String getTime() {
		String date = "";
		Calendar calendar = Calendar.getInstance();  
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
		date += String.valueOf(calendar.get(Calendar.MONTH) + 1) + "月";// 获取当前月份  
		date += String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "日";// 获取当前月份的日期号码  
		String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));  
		if("1".equals(week)){  
			date += " 星期天";  
		}else if("2".equals(week)){  
			date += " 星期一"; 
		}else if("3".equals(week)){  
			date += " 星期二"; 
		}else if("4".equals(week)){  
			date += " 星期三"; 
		}else if("5".equals(week)){  
			date += " 星期四"; 
		}else if("6".equals(week)){  
			date += " 星期五";  
		}else if("7".equals(week)){  
			date += " 星期六";  
		}  
		return date;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.prePlay) {//上一首播放
			isPlaying(true);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PRE);

		} else if (v.getId() == R.id.play) {//播放
			isPlaying(true);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);

		} else if (v.getId() == R.id.pause) {//暂停
			isPlaying(false);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PAUSE);

		} else if (v.getId() == R.id.nextPlay) {//下一首播放
			isPlaying(true);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_NEXT);

		}
	}

	/**
	 * @Title: MyReceiver   
	 * @Description: TODO 自定义播放广播接收器
	 * @author wuhuihui
	 * @date 2016年5月10日 下午2:20:31 
	 */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.i(TAG, "OnReceiver");
			//处理接收到的内容
			if(intent != null){
				if (intent.getAction() == PlayService.ACTION_PLAY) {
					isPlaying(true);

				} else if (intent.getAction() == PlayService.ACTION_PAUSE) {
					isPlaying(false);

				}
			}
		}
	}

	/** 
	 * @Title: isPlaying 
	 * @Description: TODO 根据播放暂停设置控件显示信息
	 * @param isPlaying  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月12日 下午3:59:20
	 */
	private void isPlaying(boolean isPlaying) {

		//设置基本显示
		if (MusicUtils.getLastMusic() != null) {
			song.setText(MusicUtils.getLastMusic().getArtist()+ "-" + MusicUtils.getLastMusic().getTitle());
		} else {
			song.setText("听音乐,享现在"); 
		}

		if (isPlaying) {
			pause.setVisibility(View.VISIBLE);
			play.setVisibility(View.GONE);
		} else {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(myReceiver);
			unregisterReceiver(receiver);
		}
	}

	/**
	 * 手势滑动时别调用
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
		if (e1.getX() - e2.getX() > 110) { // 向左滑动
			LogUtils.i(TAG, "向左滑动");
		} else if (e2.getX() - e1.getX() > 110) { // 向右滑动
			LogUtils.i(TAG, "向右滑动");
			finish();
		}
		return false;
	}

	/**
	 * 长按时被调用
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		LogUtils.d(TAG, "触发长按回调");
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
		LogUtils.d(TAG, "按下回调");
		return false;
	}

	/**
	 * 按住时被调用
	 */
	@Override
	public void onShowPress(MotionEvent e) {
		LogUtils.d(TAG, "按住不松回调");
	}

	/**
	 * 抬起时被调用
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		LogUtils.d(TAG, "触发抬起回调");
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

}
