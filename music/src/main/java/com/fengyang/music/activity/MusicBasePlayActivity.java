package com.fengyang.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.adapter.MusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Title: Base_PlayActivity
 * @Description: TODO 所有播放界面的Activity的基类，处理共同的操作，如:播放/暂停/下一首按钮
 * @author wuhuihui
 * @date 2016年5月6日 下午4:40:24
 */
public class MusicBasePlayActivity extends MusicBaseActivity {

	protected TextView song, singer;//底部歌名，底部歌手
	protected ImageButton play, pause;//播放/暂停按钮
	private MyReceiver myReceiver;//内部接受器，控制界面指令

	protected ListView listView;
	protected MusicAdapter adapter;
	protected TextView footerView;//listView头部音乐数
	protected ProgressBar progressBar;//进度条
	private Timer playTimer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView("懒猫音乐", R.layout.activity_play_base);//独立。用于音乐播放界面设置底部控制播放暂停等操作

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		song = (TextView) findViewById(R.id.song);
		singer = (TextView) findViewById(R.id.singer);

		play = (ImageButton) findViewById(R.id.play);
		pause = (ImageButton) findViewById(R.id.pause);

		LinearLayout bottomView = (LinearLayout) findViewById(R.id.bottomView);
		bottomView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {//跳转音乐详情页
				LogUtils.i(TAG, "bottomView");
				if (MusicUtils.getLastMusic() != null) {
					startActivity(new Intent(activity, PlayActivity.class));
				} else {
					StringUtils.show1Toast(context, "请先选取音乐播放");
				}

			}
		});

		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.footetview, null);
		footerView = (TextView) view.findViewById(R.id.footerView);

		myReceiver = new MyReceiver();
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction(PlayService.ACTION_PLAY);
		myFilter.addAction(PlayService.ACTION_NEXT);
		myFilter.addAction(PlayService.ACTION_PAUSE);
		registerReceiver(myReceiver, myFilter);

		setRightBtnListener("设置", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), SettingActivity.class));
			}
		});

		//获取上次播放的音乐信息及播放进度
		Music lastMusic = MusicUtils.getLastMusic();
		if (lastMusic != null) {
			LogUtils.i(TAG, "lastMusic---" + lastMusic.getTitle());
			song.setText(lastMusic.getTitle());
			singer.setText(lastMusic.getArtist());
		} else {
			LogUtils.i(TAG, "none play history~~~~");
			song.setText("听音乐");
			singer.setText("享现在");
		}

		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.GONE);
	}

	/**
	 * 设置中间内容布局
	 * @param title
	 * @param layoutID
	 */
	protected void setContentView4Play(String title, int layoutID) {

		//设置当前界面的title
		TextView titleView = (TextView) findViewById(com.fengyang.toollib.R.id.title);
		titleView.setText(title);

		//加载中间布局
		FrameLayout content_layout = (FrameLayout) findViewById(R.id.play_content_layout);
		content_layout.removeAllViews();
		View view = LayoutInflater.from(this).inflate(layoutID, null);
		content_layout.addView(view);
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
			if (MusicUtils.getLastMusic() != null) {
				if (MusicUtils.getLastMusic().getId()  == MusicUtils.list.size()) {
					listView.setSelection(1);
				} else {
					listView.setSelection(MusicUtils.getLastMusic().getId() + 1);
				}
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);

			//更新播放进度
			if (PlayService.media != null) {
				int progress = PlayService.media.getCurrentPosition();  //当前播放毫秒
				int allTime = PlayService.media.getDuration();      //总毫秒             
				progressBar.setMax(allTime);//设置进度条
				progressBar.setProgress(progress);
//				LogUtils.i(TAG, "更新播放进度" + StringUtils.getTimeFormat(progress));
				MusicUtils.setLastMusicProgress(progress);//更新记录音乐播放进度
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
			Message msg = Message.obtain();
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPlaying(PlayService.isPlaying());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.play) {//播放
			isPlaying(true);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);

		} else if (v.getId() == R.id.pause) {//暂停
			isPlaying(false);
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PAUSE);

		} else if (v.getId() == R.id.next) {//下一首播放
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_NEXT);
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
				LogUtils.i(TAG, "OnReceiver---" + intent.getAction());
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
		MusicUtils.setTimerNull(playTimer);

		//底部菜单显示当前音乐信息
		if (MusicUtils.getLastMusic() != null) {
			song.setText(MusicUtils.getLastMusic().getTitle());
			singer.setText(MusicUtils.getLastMusic().getArtist());
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
		MusicUtils.setTimerNull(playTimer);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myReceiver != null)  unregisterReceiver(myReceiver);
	}
}
