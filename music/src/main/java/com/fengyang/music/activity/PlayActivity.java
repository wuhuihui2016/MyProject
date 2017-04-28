package com.fengyang.music.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.music.R;
import com.fengyang.music.adapter.PlayMusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.ActivityUtils;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.DBUtils;
import com.fengyang.music.utils.ToolUtils;
import com.fengyang.music.view.ImageTextButtonView;

/**
 * @Title: PlayActivity   
 * @Description: TODO 播放音乐详情页
 * @author wuhuihui
 * @date 2016年5月12日 下午4:03:23 
 */
public class PlayActivity extends BaseActivity implements OnClickListener, OnSeekBarChangeListener {

	private ImageView imageView, anim;
	private WebView webView;

	private TextView tvTitle, start, end;//头部页面标题，底部开始时间，底部结束时间
	private ImageButton isLike, modecircle, modeorder, moderandom, modesingle, play, pause;
	private ImageTextButtonView iTButView;
	private SeekBar seekBar;//进度条
	private Timer playTimer = null;
	private PopupWindow popupWindow;
	private MyReceiver myReceiver;//内部接收器，控制界面指令
	private DBUtils utils;
	private Music lastMusic;

	@SuppressLint("InlinedApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//屏幕保持常亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_play);

		//向右滑动手势
		LinearLayout playLayout = (LinearLayout) findViewById(R.id.playLayout);
		playLayout.setOnTouchListener(this);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		iTButView = (ImageTextButtonView) findViewById(R.id.iTButView);
		isLike = (ImageButton) findViewById(R.id.isLike);
		start = (TextView) findViewById(R.id.start);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		end = (TextView) findViewById(R.id.end);
		modecircle = (ImageButton) findViewById(R.id.modecircle);
		modeorder = (ImageButton) findViewById(R.id.modeorder);
		moderandom = (ImageButton) findViewById(R.id.moderandom);
		modesingle = (ImageButton) findViewById(R.id.modesingle);
		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);
		imageView = (ImageView) findViewById(R.id.icon);
		webView = (WebView) findViewById(R.id.gif);
		anim = (ImageView) findViewById(R.id.anim);

		myReceiver = new MyReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(PlayService.ACTION_PLAY);
		myFilter.addAction(PlayService.ACTION_NEXT);
		myFilter.addAction(PlayService.ACTION_PAUSE);
		myFilter.addAction(PlayService.ACTION_SETTIME);
		registerReceiver(myReceiver, myFilter);

		//获取当前播放状态
		if (PlayService.media == null) isPlaying(false); 
		else isPlaying(PlayService.media.isPlaying());

		setModeView(ContansUtils.getPlayMode());//设置控件显示当前播放模式

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
		ToolUtils.setTimerNull(playTimer);
	}

	@SuppressLint("HandlerLeak") Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			//更新播放进度
			if (PlayService.media != null) {
				int progress = PlayService.media.getCurrentPosition();  //当前播放毫秒
				int allTime = PlayService.media.getDuration();      //总毫秒             
				seekBar.setMax(allTime);//设置进度条
				seekBar.setProgress(progress);
				start.setText(ToolUtils.getTimeFormat(progress));
				end.setText(ToolUtils.getTimeFormat(allTime));
			}
		};
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_back){
			ActivityUtils.finishAllActivity();
			startActivity(new Intent(getApplicationContext(), MusicActivity.class));

		} else if (v.getId() == R.id.btn_setting) {//跳转设置页
			startActivity(new Intent(this, SettingActivity.class));

		} else if (v.getId() == R.id.modecircle) {//列表播放
			setModeView(Music.mode_order);
			ContansUtils.setPlayMode(getApplicationContext(), Music.mode_order);
			Toast.makeText(getApplicationContext(), "列表播放", Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.modeorder) {//随机播放
			setModeView(Music.mode_random);
			ContansUtils.setPlayMode(getApplicationContext(), Music.mode_random);
			Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.moderandom) {//单首循环
			setModeView(Music.mode_single);
			ContansUtils.setPlayMode(getApplicationContext(), Music.mode_single);
			Toast.makeText(getApplicationContext(), "单首循环", Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.modesingle) {//循环播放
			setModeView(Music.mode_circle);
			ContansUtils.setPlayMode(getApplicationContext(), Music.mode_circle);
			Toast.makeText(getApplicationContext(), "循环播放", Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.prePlay) {//上一首播放
			isPlaying(true);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PRE);

		} else if (v.getId() == R.id.play) {//播放
			isPlaying(true);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);

		} else if (v.getId() == R.id.pause) {//暂停
			isPlaying(false);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PAUSE);

		} else if (v.getId() == R.id.nextPlay) {//下一首播放
			isPlaying(true);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_NEXT);

		} else if (v.getId() == R.id.listPlay) {//显示列表
			View layout = LayoutInflater.from(PlayActivity.this).inflate(R.layout.play_popupwindows, null);

			ListView playList = (ListView) layout.findViewById(R.id.playList);
			Button close = (Button) layout.findViewById(R.id.close);
			//背景色#FFFFF4
			popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, true);
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.setOutsideTouchable(false);
			popupWindow.setFocusable(true);// 设置是否允许在外点击使其消失
			popupWindow.setBackgroundDrawable(new BitmapDrawable());//必须设置背景否则关闭不了
			popupWindow.showAtLocation(findViewById(R.id.playLayout), Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);

			TextView topText = new TextView(getApplicationContext());
			topText.setTextColor(Color.GRAY);
			topText.setText("   播放队列（" + ContansUtils.list.size() + "）");
			playList.addHeaderView(topText);

			playList.setAdapter(new PlayMusicAdapter(ContansUtils.list, getApplicationContext()));

			if (ContansUtils.getLastMusic() != null) {
				if (ContansUtils.getLastMusic().getId()  == ContansUtils.list.size()) {
					playList.setSelection(1);
				} else {
					playList.setSelection(ContansUtils.getLastMusic().getId());
				}
			}
		
			playList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					popupWindow.dismiss();
					ContansUtils.setLastMusic(getApplicationContext(), ContansUtils.list.get(position - 1), 0);

					ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);

				}
			});

			close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {if (popupWindow.isShowing()) {popupWindow.dismiss();}}
			});
		} else if (v.getId() == R.id.isLike) {
			if (utils.isLiked(lastMusic)) {
				isLike.setImageResource(R.drawable.unlike);
				utils.delMusic(lastMusic);
				Toast.makeText(getApplicationContext(), "任性退回！", Toast.LENGTH_SHORT).show();
			} else {
				isLike.setImageResource(R.drawable.liked);
				utils.addMusic(lastMusic);
				Toast.makeText(getApplicationContext(), "收入禳中！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** 
	 * @Title: setModeView 
	 * @Description: TODO 设置4个播放模式控件是否可见
	 * @param mode  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月13日 下午2:40:49
	 */
	private void setModeView(int mode) {
		if (mode == Music.mode_circle) {
			modecircle.setVisibility(View.VISIBLE);
			modeorder.setVisibility(View.GONE);
			moderandom.setVisibility(View.GONE);
			modesingle.setVisibility(View.GONE);
		} else if (mode == Music.mode_order) {
			modecircle.setVisibility(View.GONE);
			modeorder.setVisibility(View.VISIBLE);
			moderandom.setVisibility(View.GONE);
			modesingle.setVisibility(View.GONE);
		} else if (mode == Music.mode_random) {
			modecircle.setVisibility(View.GONE);
			modeorder.setVisibility(View.GONE);
			moderandom.setVisibility(View.VISIBLE);
			modesingle.setVisibility(View.GONE);
		} else if (mode == Music.mode_single) {
			modecircle.setVisibility(View.GONE);
			modeorder.setVisibility(View.GONE);
			moderandom.setVisibility(View.GONE);
			modesingle.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @Title: MyReceiver   
	 * @Description: TODO 自定义一个广播接收器
	 * @author wuhuihui
	 * @date 2016年5月10日 下午2:20:31 
	 */
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "OnReceiver");
			//处理接收到的内容
			if(intent != null){
				if (intent.getAction() == PlayService.ACTION_PLAY) {
					isPlaying(true);

				} else if (intent.getAction() == PlayService.ACTION_PAUSE) {
					isPlaying(false);
					ToolUtils.setTimerNull(playTimer);

				} else if (intent.getAction() == PlayService.ACTION_SETTIME){
					setTimeInfo();
				}
			}
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

		if (ContansUtils.setTime && ContansUtils.lastTime != 0) {
			iTButView.setVisibility(View.VISIBLE);
			iTButView.setTextViewText(
					ToolUtils.getTimeFormat(ContansUtils.lastTime * 1000));
			
			iTButView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					startActivity(new Intent(getApplicationContext(), SetTimeActivity.class));
					return false;
				}
			});
		} else {
			iTButView.setVisibility(View.GONE);
		}
	}

	/**
	 * @Title: MyTask 刷新进度条
	 * @Description: TODO 
	 * @author wuhuihui
	 * @date 2016年5月6日 下午5:18:09 
	 */
	private class MyTask extends TimerTask {

		@Override
		public void run() {
			Log.i(TAG, "更新播放进度........");
			handler.sendMessage(new Message());
		}
	}

	@Override
	public void onProgressChanged(android.widget.SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {//判断用户拖动
			if (PlayService.media != null) {
				PlayService.media.seekTo(progress);//避免出现重音
				ContansUtils.setLastMusic(getApplicationContext(), ContansUtils.getLastMusic(), progress);
			}
		}
	}

	@Override
	public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}

	/** 
	 * @Title: isPlaying 
	 * @Description: TODO 根据播放暂停设置控件显示信息
	 * @param isPlaying  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月12日 下午3:59:20
	 */
	private void isPlaying(boolean isPlaying) {

		ToolUtils.setTimerNull(playTimer);
		utils = new DBUtils(getApplicationContext());
		lastMusic = ContansUtils.getLastMusic();

		//设置基本显示
		if (lastMusic != null) {
			if (lastMusic.getTitle() != null) {
				tvTitle.setText(lastMusic.getTitle() + "-" + lastMusic.getArtist()); 
				if (utils.isLiked(lastMusic)) {
					isLike.setImageResource(R.drawable.liked);
				} else {
					isLike.setImageResource(R.drawable.unlike);
				}
			} else {
				tvTitle.setText("听音乐" + "-" + "享现在"); 
			}
		}

		isLike.setOnClickListener(this);

		if (isPlaying) {

			pause.setVisibility(View.VISIBLE);
			play.setVisibility(View.GONE);
			imageView.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
			webView.loadDataWithBaseURL(null,
					"<HTML><body bgcolor='#456577'><div align=center><IMG src='http://s8.sinaimg.cn/middle/60d2061fg6e1469cc6967&amp;690' width='190' height='187'/></div></body></html>","text/html", 
					"UTF-8",null);
			//anim.setVisibility(View.VISIBLE);

			//启动事件更新及进度条更新任务，每0.5s更新一次
			playTimer = new Timer();
			playTimer.schedule(new MyTask(), 0, 1000);

		} else {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			anim.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myReceiver != null)  unregisterReceiver(myReceiver);
	}


}
