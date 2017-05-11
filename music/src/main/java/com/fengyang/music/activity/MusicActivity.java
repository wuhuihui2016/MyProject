package com.fengyang.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.adapter.MusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicDBUtils;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.utils.LogUtils;

import java.util.List;

/**
 * @Title: MusicActivity
 * @Description: TODO 主界面，所有音乐及喜欢的音乐切换
 * @author wuhuihui
 * @date 2016年6月3日 下午2:12:49 
 */
public class MusicActivity extends MusicBasePlayActivity {

	private Button all, liked;//切换的按钮
	private TextView muido;//随机播放/批量操作
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView4Play("懒猫音乐", R.layout.activity_music_main);

		MusicUtils.getMusicList(context);

		all = (Button) findViewById(R.id.all);
		all.setOnClickListener(this);
		liked = (Button) findViewById(R.id.liked);
		liked.setOnClickListener(this);
		muido = (TextView) findViewById(R.id.muido);
		muido.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.musicListView);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.all) {
			select(1);
		} else if (v.getId() == R.id.liked){
			select(2);
		} else if (v.getId() == R.id.play4list) {//全部播放
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY_ALL);

		} else if (v.getId() == R.id.muido) {
			Intent intent = new Intent(getApplicationContext(), MuiMusicActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}
	}

	/** 
	 * @Title: select 
	 * @Description: TODO 列表选择显示
	 * @param i  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月1日 下午3:24:33
	 */
	private void select(int i) {
		index = i;
		if (i == 1) {//获取所有音乐
			all.setBackgroundResource(R.drawable.shape_app_color);
			all.setTextColor(getResources().getColor(R.color.white));
			liked.setBackgroundResource(R.drawable.shape_app);
			liked.setTextColor(getResources().getColor(R.color.app_color));

			if (MusicUtils.list.size() > 0) {
				LogUtils.i("select", MusicUtils.list.toString());
				listView.removeFooterView(footerView);//添加头布局前需remove之前添加的View
				footerView.setText("共有" + MusicUtils.list.size() + "首音乐");
				listView.addFooterView(footerView);
				adapter = new MusicAdapter(MusicUtils.list, getApplicationContext());
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						MusicUtils.setLastMusic(MusicUtils.list.get(position), 0);
						MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
					}
				});
			}

		} else if (i == 2) {//获取喜欢的音乐列表
			all.setBackgroundResource(R.drawable.shape_app);
			all.setTextColor(getResources().getColor(R.color.app_color));
			liked.setBackgroundResource(R.drawable.shape_app_color);
			liked.setTextColor(getResources().getColor(R.color.white));

			MusicDBUtils utils = new MusicDBUtils(getApplicationContext());
			List<Music> liskedMusics = utils.getLikedList();
			LogUtils.i(TAG, "喜欢的音乐列表---" + liskedMusics.toString());
			if (MusicUtils.list.size() > 0) {
				listView.removeFooterView(footerView);//添加头布局前需remove之前添加的View
				footerView.setText("共有" + liskedMusics.size() + "首喜欢的音乐");
				listView.addFooterView(footerView);
				adapter = new MusicAdapter(liskedMusics, getApplicationContext());
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						MusicUtils.setLastMusic(MusicUtils.list.get(position), 0);
						MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
					}
				});
			} 
		}

		//定位到当前音乐
		if (MusicUtils.getLastMusic() != null) {
			if (MusicUtils.getLastMusic().getId()  == MusicUtils.list.size()) {
				listView.setSelection(1);
			} else {
				listView.setSelection(MusicUtils.getLastMusic().getId());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		select(1);
	}

}
