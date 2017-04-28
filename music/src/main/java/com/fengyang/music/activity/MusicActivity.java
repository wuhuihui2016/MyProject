package com.fengyang.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.DBUtils;
import com.fengyang.music.utils.ToolUtils;

import java.util.List;

/**
 * @Title: MusicActivity
 * @Description: TODO 主界面，所有音乐及喜欢的音乐切换
 * @author wuhuihui
 * @date 2016年6月3日 下午2:12:49 
 */
@SuppressLint("ResourceAsColor") public class MusicActivity extends Base_PlayActivity {

	private Button all, liked;//切换的按钮
	private TextView muido;//随机播放/批量操作
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(this, "我的音乐");

		Log.i(TAG, "手动启动服务");
		startService(new Intent(getApplicationContext(), PlayService.class));

		View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main, null);
		setView(contentView);

		btn_back.setVisibility(View.INVISIBLE);
		all = (Button) contentView.findViewById(R.id.all);
		all.setOnClickListener(this);
		liked = (Button) contentView.findViewById(R.id.liked);
		liked.setOnClickListener(this);
		muido = (TextView) findViewById(R.id.muido);
		muido.setOnClickListener(this);

		listView = (ListView) contentView.findViewById(R.id.musicListView);
		
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.all) {
			select(1);
		} else if (v.getId() == R.id.liked){
			select(2);
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
			all.setBackgroundColor(Color.parseColor("#1AFD9C"));
			liked.setBackgroundColor(Color.parseColor("#E8FFF5"));

			if (ContansUtils.list.size() > 0) {
				listView.removeFooterView(footerView);//添加头布局前需remove之前添加的View
				footerView.setText("共有" + ContansUtils.list.size() + "首音乐");
				listView.addFooterView(footerView);
				adapter = new MusicAdapter(ContansUtils.list, getApplicationContext());
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ContansUtils.setLastMusic(getApplicationContext(), ContansUtils.list.get(position), 0);
						ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
					}
				});
			}

		} else if (i == 2) {//获取喜欢的音乐列表
			all.setBackgroundColor(Color.parseColor("#E8FFF5"));
			liked.setBackgroundColor(Color.parseColor("#1AFD9C"));
			DBUtils utils = new DBUtils(getApplicationContext());
			List<Music> liskedMusics = utils.getLikedList();
			Log.i(TAG, "喜欢的音乐列表---" + liskedMusics.toString());
			if (ContansUtils.list.size() > 0) {
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
						ContansUtils.setLastMusic(getApplicationContext(), ContansUtils.list.get(position), 0);
						ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
					}
				});
			} 
		}

		//定位到当前音乐
		if (ContansUtils.getLastMusic() != null) {
			if (ContansUtils.getLastMusic().getId()  == ContansUtils.list.size()) {
				listView.setSelection(1);
			} else {
				listView.setSelection(ContansUtils.getLastMusic().getId());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		select(1);
	}

}
