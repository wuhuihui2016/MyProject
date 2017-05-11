package com.fengyang.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.model.Music;

import java.util.List;
/**
* @Title: MusicAdapter   
* @Description: TODO 音乐适配器
* @author wuhuihui
* @date 2016年4月28日 下午5:25:01 
*/
public class MusicAdapter extends BaseAdapter {

	Context context;
	List<Music> list;

	public MusicAdapter(List<Music> list,Context context) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.song = (TextView) convertView.findViewById(R.id.song);
			viewHolder.singer = (TextView) convertView.findViewById(R.id.singer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Music music = list.get(position);
		viewHolder.song.setText(music.getTitle());
		viewHolder.singer.setText(music.getArtist() + "   " + music.getAlbum());
		return convertView;
	}

	class ViewHolder{
		TextView song, singer;
	}

}
