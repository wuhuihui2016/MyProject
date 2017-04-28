package com.fengyang.music.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.music.R;
import com.fengyang.music.adapter.MusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.ToolUtils;

/**
 * @Title: Base_PlayActivity   
 * @Description: TODO 所有播放界面的Activity的基类，处理共同的操作，如:播放/暂停/下一首按钮
 * @author wuhuihui  
 * @date 2016年5月6日 下午4:40:24
 */
public class Base_PlayActivity extends BaseActivity implements OnClickListener {

	protected TextView song, singer;//头部页面标题，底部歌名，底部歌手
	protected ImageButton btn_back, btn_setting;
	protected FrameLayout frameLayout;//中间位置的显示其他控件的布局
	protected ImageButton play, pause;//播放/暂停按钮
	private MyReceiver myReceiver;//内部接受器，控制界面指令

	protected ListView listView;
	protected MusicAdapter adapter;
	protected TextView footerView;//listView头部音乐数
	protected ProgressBar progressBar;//进度条
	private Timer playTimer = null;
	
	@SuppressLint("InlinedApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_play);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_setting = (ImageButton) findViewById(R.id.btn_setting);
		song = (TextView) findViewById(R.id.song);
		singer = (TextView) findViewById(R.id.singer);
		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);

		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.footetview, null);
		footerView = (TextView) view.findViewById(R.id.footerView);

		myReceiver = new MyReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(PlayService.ACTION_PLAY);
		myFilter.addAction(PlayService.ACTION_NEXT);
		myFilter.addAction(PlayService.ACTION_PAUSE);
		registerReceiver(myReceiver, myFilter);

		//获取上次播放的音乐信息及播放进度
		Music lastMusic = ContansUtils.getLastMusic();
		if (lastMusic != null) {
			Log.i(TAG, "lastMusic---" + lastMusic.getTitle());
			song.setText(lastMusic.getTitle());
			singer.setText(lastMusic.getArtist());
		} else {
			Log.i(TAG, "none play history~~~~");
			song.setText("听音乐");
			singer.setText("享现在");
		}

		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.GONE);
	}
	
	/** 
	* @Title: setSelection 
	* @Description: TODO 动态定位到列表中的当前音乐  
	* @return void
	* @author wuhuihui  
	* @date 2016年6月12日 下午5:09:14
	*/
	private void setSelection() {
		if (TAG.contains("MusicActivity")) {
			if (ContansUtils.getLastMusic() != null) {
				if (ContansUtils.getLastMusic().getId()  == ContansUtils.list.size()) {
					listView.setSelection(1);
				} else {
					listView.setSelection(ContansUtils.getLastMusic().getId() + 1);
				}
			}
		}
	}
	
	@SuppressLint("HandlerLeak") Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);

			//更新播放进度
			if (PlayService.media != null) {
				int progress = PlayService.media.getCurrentPosition();  //当前播放毫秒
				int allTime = PlayService.media.getDuration();      //总毫秒             
				progressBar.setMax(allTime);//设置进度条
				progressBar.setProgress(progress);
				Log.i(TAG, "更新播放进度" + ToolUtils.getTimeFormat(progress));
				ContansUtils.setLastMusicProgress(getApplicationContext(), progress);//更新记录音乐播放进度
			}
		
		};
	};
	
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
			Message msg = Message.obtain();
			handler.sendMessage(msg);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isPlaying(PlayService.isPlaying());
	}

	/** 
	 * @Title: setTitle 
	 * @Description: TODO 设置title_bar的标题文字
	 * @param activity
	 * @param titleStr  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setTitle(Activity activity, String titleStr){
		tvTitle = (TextView) activity.findViewById(R.id.tv_title);
		tvTitle.setText(titleStr);
	}

	/** 
	 * @Title: setTitle 
	 * @Description: TODO 设置title_bar的标题文字
	 * @param activity
	 * @param titleResourceId  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setTitle(Activity activity, int titleResourceId){
		setTitle(activity, getString(titleResourceId));
	}

	/** 
	 * @Title: setView 
	 * @Description: TODO 设置中间布局控件
	 * @param v  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setView(View v) {
		frameLayout.removeAllViews();
		frameLayout.addView(v);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_back){
			finish();

		} else if (v.getId() == R.id.btn_setting) {//跳转搜索页
			startActivity(new Intent(this, SettingActivity.class));

		} else if (v.getId() == R.id.bottomView) {//跳转音乐详情页
			if (ContansUtils.getLastMusic() != null) {
				startActivity(new Intent(this, PlayActivity.class));
			} else {
				Toast.makeText(getApplicationContext(), "请先选取音乐播放", Toast.LENGTH_SHORT).show();
			}

		} else if (v.getId() == R.id.play) {//播放
			isPlaying(true);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);

		} else if (v.getId() == R.id.pause) {//暂停
			isPlaying(false);
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PAUSE);

		} else if (v.getId() == R.id.next) {//下一首播放
			ToolUtils.startService(getApplicationContext(), PlayService.ACTION_NEXT);
			setSelection();
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
			//处理接收到的内容
			if(intent != null){
				Log.i(TAG, "OnReceiver---" + intent.getAction());
				isPlaying(PlayService.isPlaying());
			}
		}
	}

	/** 
	* @Title: isPlaying 
	* @Description: TODO 根据播放状态设置控件信息
	* @param isPlaying  
	* @return void
	* @author wuhuihui  
	* @date 2016年6月2日 上午11:15:04
	*/
	private void isPlaying(boolean isPlaying) {
		ToolUtils.setTimerNull(playTimer);
		
		//底部菜单显示当前音乐信息
		if (ContansUtils.getLastMusic() != null) {
			song.setText(ContansUtils.getLastMusic().getTitle());
			singer.setText(ContansUtils.getLastMusic().getArtist());
		}
		if (isPlaying) {
			pause.setVisibility(View.VISIBLE);
			play.setVisibility(View.GONE);
			
			playTimer = new Timer();
			playTimer.schedule(new MyTask(), 0, 1000);

		} else {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ToolUtils.setTimerNull(playTimer);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myReceiver != null)  unregisterReceiver(myReceiver);
	}
}
