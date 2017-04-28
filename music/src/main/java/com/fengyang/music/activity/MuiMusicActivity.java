package com.fengyang.music.activity;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengyang.music.R;
import com.fengyang.music.adapter.MuiMusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.DBUtils;

public class MuiMusicActivity extends Base_SetActivity {

	private CheckBox allCheckBox;
	private TextView info, collect, delete;
	private ListView listView;
	private MuiMusicAdapter adapter;
	private DBUtils utils;

	private List<Music> list;//数据列表
	private int index;//加载数据内容的标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_muimusic, null);
		setView(contentView);

		RelativeLayout layout = (RelativeLayout) contentView.findViewById(R.id.layout);
		layout.setOnTouchListener(this);

		index = getIntent().getIntExtra("index", 0);
		Log.i(TAG, "加载index---" + index);
		if (index == 1) setTitle(this, "本地音乐");
		else setTitle(this, "喜欢的音乐");
		utils = new DBUtils(getApplicationContext());

		listView = (ListView) contentView.findViewById(R.id.listView);

		allCheckBox = (CheckBox) contentView.findViewById(R.id.allCheckBox);
		allCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) Log.i(TAG, "全选啦");
				else Log.i(TAG, "取消全选啦");
				adapter.selectAll(listView, isChecked);
				info.setText(String.format("已选%d/%d首音乐", 
						adapter.selList.size(), list.size()));
				adapter.notifyDataSetChanged();
				
			}
		});
		
		getData();//获取数据

		info = (TextView) contentView.findViewById(R.id.info);
		info.setText("已选0/" + list.size() + "首音乐");
		collect = (TextView) contentView.findViewById(R.id.collect);
		if (index == 2) collect.setText("取消收藏");
		collect.setOnClickListener(this);
		delete = (TextView) contentView.findViewById(R.id.delete);
		delete.setOnClickListener(this);
	}

	/** 
	 * @Title: getData 
	 * @Description: TODO  获取列表数据
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月17日 上午10:57:52
	 */
	private void getData() {
		if (index == 1) list = ContansUtils.list; else list = utils.getLikedList();
		adapter = new MuiMusicAdapter(list, getApplicationContext());
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelected(listView, position, list.get(position));
				info.setText(String.format("已选%d/%d首音乐", 
						adapter.selList.size(), list.size()));
			}
		});
		
		if (allCheckBox.isChecked())  allCheckBox.setChecked(false);
	}

	/** 
	 * @Title: showDelDialog 
	 * @Description: TODO 显示自定义睡眠时间对话框  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月19日 下午4:06:25
	 */
	private void showDelDialog() {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = null;
		View layout =  LayoutInflater.from(MuiMusicActivity.this).inflate(R.layout.del_dialog, null);
		final CheckBox delCheckBox = (CheckBox) layout.findViewById(R.id.delCheckBox);
		builder = new AlertDialog.Builder(MuiMusicActivity.this);
		builder.setView(layout);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				List<Music> list = adapter.selList;
				if (list.size() > 0) {
					if (delCheckBox.isChecked()) {//在列表移除并删除本地文件
						for (int i = 0; i < list.size(); i++) {
							Log.i(TAG, "delete music file --- " + list.get(i).getUrl());
							Log.i(TAG, "lastMusic" + ContansUtils.getLastMusic().toString());
							if (PlayService.isPlaying() && ContansUtils.getLastMusic().getId() == list.get(i).getId()) {
								//如果删除的是正在播放的歌曲，则不删除
								Toast.makeText(getApplicationContext(), 
										list.get(i).getTitle() + " 正在播放,稍后操作...", Toast.LENGTH_SHORT).show();
							} else {
								File file = new File(list.get(i).getUrl());
								if (file.exists()) file.delete();
							}
						}
						if (! new File(list.get(0).getUrl()).exists())
							Toast.makeText(getApplicationContext(), "本地文件删除成功！", Toast.LENGTH_SHORT).show();

					} else {//仅在列表移除
						for (int i = 0; i < list.size(); i++) {
							for (int j = 0; j < ContansUtils.list.size(); j++) {
								if (list.get(i).getId() == ContansUtils.list.get(j).getId() ) {
									ContansUtils.list.remove(ContansUtils.list.get(j));
								}
							}
						}
						Toast.makeText(getApplicationContext(), "移除音乐成功！", Toast.LENGTH_SHORT).show();
					}
					
					getData();//重新加载数据
				} else {
					Toast.makeText(getApplicationContext(), "请选取音乐", Toast.LENGTH_SHORT).show();
				}
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.collect) {
			List<Music> list = adapter.selList;
			if (list.size() > 0) {
				if (index == 1) {//收藏所选音乐
					for (int i = 0; i < list.size(); i++) {
						utils.addMusic(list.get(i));
					}
					Toast.makeText(getApplicationContext(), "收入禳中！", Toast.LENGTH_SHORT).show();
				} else {//取消收藏所选音乐
					for (int i = 0; i < list.size(); i++) {
						utils.delMusic(list.get(i));
					}
					Toast.makeText(getApplicationContext(), "任性退回！", Toast.LENGTH_SHORT).show();
					getData();//重新加载数据
				}
			} else {
				Toast.makeText(getApplicationContext(), "请选取音乐", Toast.LENGTH_SHORT).show();
			}
			getData();
		} else if (v.getId() == R.id.delete) {//删除所选音乐
			if (adapter.selList.size() > 0) {
				showDelDialog();
			} else {
				Toast.makeText(getApplicationContext(), "请选取音乐", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
