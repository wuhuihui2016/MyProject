package com.fengyang.music.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.adapter.HistoryAdapter;
import com.fengyang.music.adapter.MusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.ContansUtils;
import com.fengyang.music.utils.ToolUtils;
import com.fengyang.music.view.FlowLayout;
import com.fengyang.music.view.SearchView;

/**
 * @Title: SearchActivity   
 * @Description: TODO 任意搜索本地音乐文件
 * @author wuhuihui
 * @date 2016年6月3日 下午2:21:29 
 */
public class SearchActivity extends Base_PlayActivity {

	private View contentView;
	private SearchView searchView;
	private ImageButton clearHistory;
	private LinearLayout linearLayout, historyLayout;
	private FlowLayout flowLayout;
	private ListView historyListView;

	private String key;//搜索关键词
	private List<Music> searchList = new ArrayList<Music>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*当在Android的layout设计里面如果输入框过多，则在输入弹出软键盘的时候，下面的输入框会有一部分被软件盘挡住，从而不能获取焦点输入。
		解决办法：
		方法一：在你的activity中的oncreate中setContentView之前写上这个代码 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		方法二：在项目的AndroidManifest.xml文件中界面对应的<activity>里加入 android:windowSoftInputMode="stateVisible|adjustResize"，这样会让屏幕整体上移。如果加上的是
		                android:windowSoftInputMode="adjustPan"这样键盘就会覆盖屏幕。*/
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		setTitle(this, "");
		btn_setting.setVisibility(View.GONE);
		//searchView
		searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setVisibility(View.VISIBLE);
		searchView.setHint("搜索本地音乐");
		searchView.setEditTextSize(12);
		searchView.setEditTextColor(Color.BLACK);

		contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_search, null);
		setView(contentView);

		LinearLayout layout = (LinearLayout) contentView.findViewById(R.id.layout);
		layout.setOnTouchListener(this);

		flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		historyLayout = (LinearLayout) findViewById(R.id.historyLayout);
		historyListView = (ListView) findViewById(R.id.historyListView);

		//searchListView
		listView = (ListView) contentView.findViewById(R.id.searchListView);
		listView.addFooterView(footerView);

		clearHistory = (ImageButton) contentView.findViewById(R.id.clearHistory);
		clearHistory.setOnClickListener(this);

		setSearchTag();
		isFinishedSeach(false);
		searchView.setOnFinishListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolUtils.hideInput(SearchActivity.this);
				key = searchView.getText();
				isFinishedSeach(true);
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolUtils.hideInput(SearchActivity.this);
				searchView.setText("");
				isFinishedSeach(false);
			}
		});
	}

	/** 
	 * @Title: isFinishSeach 
	 * @Description: TODO 判断当前搜索时机，之前还是之后
	 * @param isFinished  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 上午11:05:44
	 */
	private void isFinishedSeach(boolean isFinished)  {
		if (isFinished) {
			if (TextUtils.isEmpty(key)) {
				isFinishedSeach(false);
			} else {
				//保存搜索记录，查找结果
				ContansUtils.addHistory(getApplicationContext(), key);
				searchList.clear();
				for (Music music: ContansUtils.list) {
					if (music.getAlbum().contains(key) //专辑名查找
							|| music.getArtist().contains(key) //歌手查找
							|| music.getTitle().contains(key)) { //歌名查找
						searchList.add(music);
					}
				}

				//显示结果列表
				linearLayout.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				listView.removeHeaderView(footerView);
				listView.addFooterView(footerView);
				if (searchList.size() > 0) {
					footerView.setText("搜索到" + searchList.size() + "首音乐");
				} else {
					footerView.setText("暂无音乐！");
				}

				adapter = new MusicAdapter(searchList, getApplicationContext());
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ContansUtils.setLastMusic(getApplicationContext(), searchList.get(position), 0);
						ToolUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
					}
				});
			}
		} else {
			listView.setVisibility(View.GONE);
			linearLayout.setVisibility(View.VISIBLE);
			setSearchTag();
			final List<String> hisList = ContansUtils.getHistory();

			Log.i(TAG, "搜索历史列表---" + hisList.size() + ":" + hisList.toString());
			if (hisList.size() > 0) {
				historyLayout.setVisibility(View.VISIBLE);
				historyListView.setAdapter(new HistoryAdapter(hisList, getApplicationContext()));
				historyListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						key = hisList.get(position);
						searchView.setText(key);
						isFinishedSeach(true);
					}
				});
			} else {
				historyLayout.setVisibility(View.GONE);
			}
		}
	}

	/** 
	 * @Title: setSearchTag 
	 * @Description: TODO 设置搜索标签  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月6日 下午2:50:03
	 */
	private void setSearchTag() {
		flowLayout.removeAllViews();//避免多次执行后出现重复多余View
		List<String> tagList = new ArrayList<String>();
		int size = ContansUtils.list.size();
		Log.i(TAG, "size---" + size);
		for (int i = 0; i < size; i++) {
			if (size > 0) {
				int x = (int)(Math.random() * (size - 1));//产生一个size - 1以内的整数
				String singer = ContansUtils.list.get(x).getArtist();
				Log.i(TAG, singer);
				tagList.add(singer);
			}
		}
		
		ContansUtils.removeRepeat(tagList);//去重
		for (int i = 0; i < tagList.size(); i++) {
			if (i <= 9) {//取10个标签
				View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tagview, null);
				TextView tagView = (TextView) view.findViewById(R.id.tagView);
				tagView.setText(tagList.get(i));
				if (i == 0) tagView.setTextColor(Color.RED);
				tagView.setClickable(true);
				tagView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						key = ((TextView) v).getText().toString();
						searchView.setText(key);
						isFinishedSeach(true);
					}
				});
				flowLayout.addView(view);
			} else continue; 

		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.clearHistory) {
			Log.i(TAG, "clearHistory");
			ContansUtils.clearHistory(getApplicationContext());
			isFinishedSeach(false);
		}
	}
	
	/** 
	* @Title: toback 
	* @Description: TODO 返回键操作，先将搜索结果隐藏  
	* @return void
	* @author wuhuihui  
	* @date 2016年6月8日 下午3:10:09
	*/
	private void toback() {
		isFinishedSeach(false);
		searchView.setText(ContansUtils.getHistory()
				.get(ContansUtils.getHistory().size() - 1));
	}
	
	@Override
	public void finish() {
		if(listView.isShown()) toback();
		else super.finish();
	}

	@Override
	public void onBackPressed() {
		if(listView.isShown()) toback();
		else super.onBackPressed();
	}

}
