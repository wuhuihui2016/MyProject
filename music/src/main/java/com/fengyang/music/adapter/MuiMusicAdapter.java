package com.fengyang.music.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.model.Music;
/**
 * @Title: MusicAdapter   
 * @Description: TODO 音乐适配器
 * @author wuhuihui
 * @date 2016年4月28日 下午5:25:01 
 */
public class MuiMusicAdapter extends BaseAdapter {

	private String TAG = "MuiMusicAdapter";
	private List<Music> list;
	private Context context;
	private Music music;

	public List<Music> selList = new ArrayList<Music>();

	public MuiMusicAdapter(List<Music> list,Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/** 
	 * @Title: selectAll 
	 * @Description: TODO 全选与否
	 * @param listView
	 * @param isAll  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月15日 上午11:05:14
	 */
	public void selectAll(ListView listView,boolean isAll) {
		Log.i(TAG, "selectAll");
		selList.clear();
		if (isAll) selList.addAll(list);

		notifyDataSetChanged();
		Log.i("MuiMusicAdapter", selList.toString());
	}

	/** 
	 * @Title: setSelected 
	 * @Description: TODO 设置选取与舍弃
	 * @param listView
	 * @param position
	 * @param music  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年6月15日 上午11:01:04
	 */
	public void setSelected(ListView listView, int position, Music music) {
		if (selList.contains(music))  selList.remove(music);
		else selList.add(music);

		notifyDataSetChanged();
		Log.i(TAG, selList.toString());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.mui_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.song = (TextView) convertView.findViewById(R.id.song);
			viewHolder.singer = (TextView) convertView.findViewById(R.id.singer);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		music = list.get(position);
		viewHolder.song.setText(music.getTitle());
		viewHolder.singer.setText(music.getArtist() + "   " + music.getAlbum());
		if (selList.size() > 0) {
			if (selList.contains(music)) viewHolder.checkBox.setButtonDrawable(R.drawable.checkbox_on);
			else viewHolder.checkBox.setButtonDrawable(R.drawable.checkbox_off);
		} else viewHolder.checkBox.setButtonDrawable(R.drawable.checkbox_off);

		return convertView;
	}

	class ViewHolder{
		TextView song,singer;
		CheckBox checkBox;
	}

}
