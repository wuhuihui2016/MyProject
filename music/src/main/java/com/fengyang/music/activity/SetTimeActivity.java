package com.fengyang.music.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fengyang.music.R;
import com.fengyang.music.adapter.TimeAdapter;
import com.fengyang.music.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: SetTimeActivity   
 * @Description: TODO 设置睡眠时间页
 * @author wuhuihui
 * @date 2016年5月20日 下午2:36:04 
 */
public class SetTimeActivity extends MusicBaseActivity {

	private List<String> list;//时间选择列表
	private ListView listView;
	private TimeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView("设置睡眠时间", R.layout.activity_settime);

		listView = (ListView) findViewById(R.id.listView);
		
		adapter = new TimeAdapter(getData(), getApplicationContext(), MusicUtils.getTimeIndex());
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) MusicUtils.setTime = false;
				else if (position == 6) {
					showDialog();
				} else {
					if (position == 1) MusicUtils.setPlaytime(context, 10);
					else if (position == 2) MusicUtils.setPlaytime(context, 20);
					else if (position == 3) MusicUtils.setPlaytime(context, 30);
					else if (position == 4) MusicUtils.setPlaytime(context, 60);
					else if (position == 5) MusicUtils.setPlaytime(context, 90);
				}
				adapter.setSelected(listView, position);//避免延迟，主动设置
			}
		});
	}

	/** 
	 * @Title: showDialog 
	 * @Description: TODO 显示自定义睡眠时间对话框  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月19日 下午4:06:25
	 */
	private void showDialog() {
		AlertDialog dialog = null;
		AlertDialog.Builder builder = null;
		View layout =  LayoutInflater.from(SetTimeActivity.this).inflate(R.layout.time_dialog, null);
		final EditText time = (EditText) layout.findViewById(R.id.time);
		builder = new AlertDialog.Builder(SetTimeActivity.this);
		builder.setView(layout);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String result = time.getText().toString();
				if (result != null) {
					MusicUtils.isUserDefined = true;
					MusicUtils.setPlaytime(context, Integer.parseInt(result));
					adapter.setSelected(listView, 6);
				} else {
					Toast.makeText(getApplicationContext(), "请输入睡眠等待时间", Toast.LENGTH_SHORT).show();
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				adapter.setSelected(listView, 0);
				MusicUtils.setPlaytime(context, 0);
			}
		});

		dialog = builder.create();
		dialog.show();

	}

	/** 
	* @Title: getData 
	* @Description: TODO 写入可设定的时间列表
	* @return  
	* @return List<String>
	* @author wuhuihui  
	* @date 2016年6月3日 下午2:27:06
	*/
	private List<String> getData() {
		list = new ArrayList<String>();
		list.add("关闭");
		list.add("10分钟");
		list.add("20分钟");
		list.add("30分钟");
		list.add("1小时");
		list.add("1.5小时");
		list.add("自定义");
		return list;
	}

}
