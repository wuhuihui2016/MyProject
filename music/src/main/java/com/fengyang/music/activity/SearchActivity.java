package com.fengyang.music.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.adapter.HistoryAdapter;
import com.fengyang.music.adapter.MusicAdapter;
import com.fengyang.music.model.Music;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.view.SearchView;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.view.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: SearchActivity
 * @Description: TODO 任意搜索本地音乐文件
 * @author wuhuihui
 * @date 2016年6月3日 下午2:21:29 
 */
public class SearchActivity extends MusicBasePlayActivity {

	private SearchView searchView;
	private Button search_btn;
	private LinearLayout linearLayout, historyLayout;
	private FlowLayout flowLayout;
	private ListView historyListView;

	private List<Music> searchList = new ArrayList<Music>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}


	/**
	 * @Title: initView 初始化界面
	 * @author wuhuihui
	 */
	private void initView() {
		search_btn = (Button) findViewById(R.id.search_btn);
		search_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		searchView = (SearchView) findViewById(R.id.searchView);
		searchView.setHint("搜索本地音乐");

		flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		historyLayout = (LinearLayout) findViewById(R.id.historyLayout);
		historyListView = (ListView) findViewById(R.id.historyListView);

		//searchListView
		listView = (ListView) findViewById(R.id.searchListView);
		listView.addFooterView(footerView);

		//清除搜索记录按钮
		findViewById(R.id.clearHistory).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MusicUtils.clearHistory();
				isFinishedSeach(null);
			}
		});

		setSearchTag();
		isFinishedSeach(null);
		searchView.edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0) {
					searchView.clear.setVisibility(View.VISIBLE);
					searchView.clear.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							searchView.edit.setText("");
						}
					});
					search_btn.setText("搜索");
					search_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							isFinishedSeach(searchView.getText());
						}
					});
				} else {
					searchView.clear.setVisibility(View.GONE);
					search_btn.setText("取消");
					search_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	/**
	 * @Title: isFinishSeach
	 * @Description: TODO 判断当前搜索时机，key是否为空
	 * @param key
	 * @return void
	 * @author wuhuihui
	 * @date 2016年6月6日 上午11:05:44
	 */
	private void isFinishedSeach(String key)  {
		if (! TextUtils.isEmpty(key)) {
			//保存搜索记录，查找结果
			MusicUtils.addHistory(key);
			searchList.clear();
			for (Music music: MusicUtils.list) {
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
					MusicUtils.setLastMusic(searchList.get(position), 0);
					MusicUtils.startService(getApplicationContext(), PlayService.ACTION_PLAY);
				}
			});
		} else {
			listView.setVisibility(View.GONE);
			linearLayout.setVisibility(View.VISIBLE);
			setSearchTag();
			final List<String> hisList = MusicUtils.getHistory();

			LogUtils.i(TAG, "搜索历史列表---" + hisList.size() + ":" + hisList.toString());
			if (hisList.size() > 0) {
				historyLayout.setVisibility(View.VISIBLE);
				historyListView.setAdapter(new HistoryAdapter(hisList, getApplicationContext()));
				historyListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						String key = hisList.get(position);
						searchView.setText(key);
						isFinishedSeach(key);
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
		int size = MusicUtils.list.size();
		LogUtils.i(TAG, "size---" + size);
		for (int i = 0; i < size; i++) {
			if (size > 0) {
				int x = (int)(Math.random() * (size - 1));//产生一个size - 1以内的整数
				String singer = MusicUtils.list.get(x).getArtist();
				LogUtils.i(TAG, singer);
				tagList.add(singer);
			}
		}

		MusicUtils.removeRepeat(tagList);//去重
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
						String key = ((TextView) v).getText().toString();
						searchView.setText(key);
						isFinishedSeach(key);
					}
				});
				flowLayout.addView(view);
			} else continue;

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
		isFinishedSeach(null);
		searchView.setText(MusicUtils.getHistory()
				.get(MusicUtils.getHistory().size() - 1));
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
